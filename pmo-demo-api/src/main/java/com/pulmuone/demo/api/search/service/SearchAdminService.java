package com.pulmuone.demo.api.search.service;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.domain.SynonymDomain;
import com.pulmuone.demo.api.search.domain.UserDictionaryDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.dto.SynonymDTO;
import com.pulmuone.demo.api.search.dto.UserWordDTO;
import com.pulmuone.demo.api.search.mapper.SearchAdminMapper;
import com.pulmuone.demo.api.util.AmazonS3Util;
import com.pulmuone.demo.common.property.ElasticsearchProperty;
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

    @Autowired
    ElasticsearchProperty elasticsearchProperty;

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
        String filePath = elasticsearchProperty.getDictionaryTempPath() + "/synonyms.txt";
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
            amazonS3Util.uploadFile(file);

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

    public void createUserDictionary() {
        String filePath = elasticsearchProperty.getDictionaryTempPath() + "/userdict_ko.txt";
        File file = new File(filePath);
        FileWriter writer = null;

        try{
            writer = new FileWriter(file, false);
            List<UserDictionaryDomain> list = searchAdminMapper.selectAllUserWordList();

            FileWriter finalWriter = writer;
            list.stream().forEach(
                    s -> {
                        try {
                            finalWriter.append( s.getUserWord() +"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            finalWriter.flush();
            amazonS3Util.uploadFile(file);

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

    public List<UserDictionaryDomain> getUserWordList(String keyword) {

        return searchAdminMapper.selectUserWordList(keyword);

    }

    public int updateUserWord(UserWordDTO userWordDTO) {
        return searchAdminMapper.updateUserWord(userWordDTO);
    }

    public int insertUserWord(UserWordDTO userWordDTO) {
        return searchAdminMapper.insertUserWord(userWordDTO);
    }

    public int deleteUserWord(UserWordDTO userWordDTO) {
        return searchAdminMapper.deleteUserWord(userWordDTO);
    }
}
