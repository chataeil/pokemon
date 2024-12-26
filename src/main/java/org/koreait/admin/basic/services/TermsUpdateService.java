package org.koreait.admin.basic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.CodeValue;
import org.koreait.global.repositories.CodeValueRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class TermsUpdateService {
    private final ObjectMapper om;
    private final CodeValueRepository codeValueRepository;

    /**
     * 약관 저장
     *
     * @param terms
     */
    public void save(Terms terms){
        String code = String.format("term_%s", terms.getCode());
        CodeValue item = codeValueRepository.findById(code).orElseGet(CodeValue::new);

        try {
            String value = om.writeValueAsString(terms);
            item.setCode(code);
            item.setValue(value);
            codeValueRepository.saveAndFlush(item);
        }catch (JsonProcessingException e){

        }


    }
}
