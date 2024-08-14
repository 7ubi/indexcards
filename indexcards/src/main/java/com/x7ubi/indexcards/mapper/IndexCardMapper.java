package com.x7ubi.indexcards.mapper;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IndexCardMapper {

    List<IndexCardResponse> mapToResponses(List<IndexCard> indexCards);

    @Mapping(source = "indexcardId", target = "indexCardId")
    @Mapping(target = "question", expression = "java(String.valueOf(java.nio.charset.StandardCharsets.UTF_8.decode(java.nio.ByteBuffer.wrap(indexCard.getQuestion()))))")
    @Mapping(target = "answer", expression = "java(String.valueOf(java.nio.charset.StandardCharsets.UTF_8.decode(java.nio.ByteBuffer.wrap(indexCard.getAnswer()))))")
    IndexCardResponse mapToResponse(IndexCard indexCard);

    @Mapping(target = "question", expression = "java(java.nio.charset.StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array())")
    @Mapping(target = "answer", expression = "java(java.nio.charset.StandardCharsets.UTF_8.encode(createIndexCardRequest.getAnswer()).array())")
    IndexCard mapRequestToIndexCard(CreateIndexCardRequest createIndexCardRequest);
}
