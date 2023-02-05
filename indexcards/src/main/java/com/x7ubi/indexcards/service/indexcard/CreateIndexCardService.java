package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.response.common.ResultResponse;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateIndexCardService {

    @Transactional
    public ResultResponse createIndexCard() {
        ResultResponse resultResponse = new ResultResponse();

        return resultResponse;
    }


}
