package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.domain.SynonymDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.dto.SynonymDTO;
import com.pulmuone.demo.api.search.mapper.SearchAdminMapper;
import com.pulmuone.demo.api.util.AmazonS3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SearchAdminService {

    @Autowired
    SearchAdminMapper searchAdminMapper;

    @Autowired
    AmazonS3Util amazonS3Util;

    public List<CategoryBoostDomain> getCategoryBoostList(String keyword) {
        log.info("keyword: {}", keyword);

        return searchAdminMapper.selectCategoryBoostList(keyword);
    }

    public int updateCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO) {
        return searchAdminMapper.updateCategoryBoostScore(categoryBoostScoreDTO);
    }

    public int deleteCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO) {
        return searchAdminMapper.deleteCategoryBoostScore(categoryBoostScoreDTO);
    }

    public int insertCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO) {
        return searchAdminMapper.insertCategoryBoostScore(categoryBoostScoreDTO);
    }

    public List<SynonymDomain> getSynonymList(String keyword) {
        return searchAdminMapper.selectSynonymList(keyword);
    }

    public int updateSynonym(SynonymDTO synonymDTO) {
        return searchAdminMapper.updateSynonym(synonymDTO);
    }

    public int insertSynonym(SynonymDTO synonymDTO) {
        return searchAdminMapper.insertSynonym(synonymDTO);
    }

    public int deleteSynonym(SynonymDTO synonymDTO) {
        return searchAdminMapper.deleteSynonym(synonymDTO);
    }

    public void createSynonymDictionary() {

        String filePath = "/Users/kjlee/tmp/synonyms.txt";
        File file = new File(filePath);

        FileWriter writer = null;

        try{
            writer = new FileWriter(file, false);
            List<SynonymDomain> list = searchAdminMapper.selectAllSynonymList();

            FileWriter finalWriter = writer;
            list.stream().forEach(
                    s -> {
                        try {
                            finalWriter.append(s.getSynonym()+"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

            finalWriter.flush();

            file.setReadable(true);


            amazonS3Util.uploadFile(file);

            log.info("DONE");

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
