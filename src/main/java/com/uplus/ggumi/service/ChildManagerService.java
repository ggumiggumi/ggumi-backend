package com.uplus.ggumi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import com.uplus.ggumi.dto.child.ChildProfileResponseDto;
import com.uplus.ggumi.repository.ChildRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildManagerService {

    private final ParentService parentService;
    private final ChildRepository childRepository;

    public Long createChildProfile(String accessToken, ChildProfileRequestDto requestDto) {
        Parent parent = parentService.getAccountByToken(accessToken);

        checkChildCreationLimit(accessToken);

        Child child = Child.builder()
                .name(requestDto.getName())
                .birthday(requestDto.getBirthday())
                .gender(requestDto.getGender())
                .profileCode(requestDto.getProfileCode())
                .parent(parent)
                .build();

        return childRepository.save(child).getId();
    }

    public List<ChildProfileResponseDto> getChildProfileList(Parent parent) {
        List<Child> children = childRepository.findByParentId(parent.getId());
        if (children.isEmpty()) {
            throw new ApiException(ErrorCode.CHILDREN_NOT_EXIST);
        }
        return children.stream()
                .map(child -> ChildProfileResponseDto.builder()
                        .name(child.getName())
                        .birthday(child.getBirthday())
                        .profileCode(child.getProfileCode())
                        .gender(child.getGender())
                        .parent(child.getParent())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public ChildProfileResponseDto getChildProfile(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        return ChildProfileResponseDto.builder()
                .name(child.getName())
                .birthday(child.getBirthday())
                .profileCode(child.getProfileCode())
                .gender(child.getGender())
                .parent(child.getParent())
                .build();
    }


    public Long updateChildProfile(Long childId, ChildProfileRequestDto requestDto) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

        child.update(requestDto);
        return child.getId();
    }

    public int checkChildCreationLimit(String accessToken) {
        Parent parent = parentService.getAccountByToken(accessToken);
        List<Child> children = childRepository.findByParentId(parent.getId());
        if (children.size() >= 3) {
            throw new ApiException(ErrorCode.CHILD_CREATION_LIMIT_REACHED);
        }
        return children.size();
    }
}
