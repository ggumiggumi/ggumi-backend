package com.uplus.ggumi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.child.Child;
import com.uplus.ggumi.domain.parent.Parent;
import com.uplus.ggumi.dto.child.ChildProfileRequestDto;
import com.uplus.ggumi.dto.child.ChildProfileResponseDto;
import com.uplus.ggumi.repository.ChildRepository;
import com.uplus.ggumi.repository.FeedbackRepository;
import com.uplus.ggumi.repository.HistoryRepository;
import com.uplus.ggumi.repository.RecommendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildManagerService {
	private final ChildRepository childRepository;

	private final FeedbackRepository feedbackRepository;
	private final RecommendRepository recommendRepository;
	private final HistoryRepository historyRepository;

	public Long createChildProfile(Parent parent, ChildProfileRequestDto requestDto) {
		checkChildCreationLimit(parent);
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
				.id(child.getId())
				.name(child.getName())
				.birthday(child.getBirthday())
				.profileCode(child.getProfileCode())
				.gender(child.getGender())
				.parentId(child.getParent().getId())
				.build()
			)
			.collect(Collectors.toList());
	}

	public ChildProfileResponseDto getChildProfile(Long childId) {
		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

		return ChildProfileResponseDto.builder()
			.id(childId)
			.name(child.getName())
			.birthday(child.getBirthday())
			.profileCode(child.getProfileCode())
			.gender(child.getGender())
			.parentId(child.getParent().getId())
			.build();
	}

	public Long updateChildProfile(Long childId, ChildProfileRequestDto requestDto) {
		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));

		child.update(requestDto);
		return child.getId();
	}

	public int checkChildCreationLimit(Parent parent) {
		List<Child> children = childRepository.findByParentId(parent.getId());
		if (children.size() >= 3) {
			throw new ApiException(ErrorCode.CHILD_CREATION_LIMIT_REACHED);
		}
		return children.size();
	}

	@Transactional
	public String logicalDeleteInfo(Long childId) {
		Child child = childRepository.findById(childId)
			.orElseThrow(() -> new ApiException(ErrorCode.CHILD_NOT_EXIST));
		LocalDateTime now = LocalDateTime.now();
		// 연관 Feedback 데이터
		int updatedFeedbacks = feedbackRepository.markFeedbackAsDeletedByChildId(childId, now);
		// 연관 Recommend 데이터
		int updatedRecommends = recommendRepository.markRecommendAsDeletedByChildId(childId, now);
		// 연관 History 데이터
		int updatedHistories = historyRepository.markHistoryAsDeletedByChildId(childId, now);

		return String.format("자녀 성향 관련 데이터가 성공적으로 삭제되었습니다. (Feedbacks: %d, Recommends: %d, Histories: %d)",
			updatedFeedbacks, updatedRecommends, updatedHistories);
	}
}