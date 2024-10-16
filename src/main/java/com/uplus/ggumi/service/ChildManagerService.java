package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import com.uplus.ggumi.dto.child.ChildProfileResponseDto;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildManagerService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;

    public Long createChildProfile(Long parentId, ChildProfileRequestDto requestDto) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ApiException(ErrorCode.PARENT_NOT_EXIST));// 임시 Parent 객체 find

        checkChildCreationLimit(parentId);

        Child child = Child.builder()
                .name(requestDto.getName())
                .birthday(requestDto.getBirthday())
                .gender(requestDto.getGender())
                .profileCode(requestDto.getProfileCode())
                .parent(parent)
                .build();

        return childRepository.save(child).getId();
    }

    public List<ChildProfileResponseDto> getChildProfileList(Long parentId) {
        List<Child> children = childRepository.findByParentId(parentId);
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

    public int checkChildCreationLimit(Long parentId) {
        List<Child> children = childRepository.findByParentId(parentId);
        if (children.size() >= 3) {
            throw new ApiException(ErrorCode.CHILD_CREATION_LIMIT_REACHED);
        }
        return children.size();
    }
}
