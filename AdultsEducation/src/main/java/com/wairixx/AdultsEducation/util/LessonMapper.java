package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.lesson.LessonPreviewResponse;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonResponse;
import com.wairixx.AdultsEducation.model.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {
    public LessonResponse toResponse(Lesson l) {
        return new LessonResponse(l.getId(), l.getTitle(), l.getContent(),
                l.getVideoUrl(), l.getOrderNumber(), l.getCourse().getId());
    }

    public LessonPreviewResponse toPreview(Lesson l) {
        return new LessonPreviewResponse(l.getId(), l.getTitle(), l.getOrderNumber());
    }
}