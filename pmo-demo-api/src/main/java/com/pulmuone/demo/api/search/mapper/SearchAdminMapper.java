package com.pulmuone.demo.api.search.mapper;

import com.pulmuone.demo.api.search.domain.CategoryBoostDomain;
import com.pulmuone.demo.api.search.domain.StopWordDictionaryDomain;
import com.pulmuone.demo.api.search.domain.SynonymDomain;
import com.pulmuone.demo.api.search.domain.UserDictionaryDomain;
import com.pulmuone.demo.api.search.dto.CategoryBoostScoreDTO;
import com.pulmuone.demo.api.search.dto.StopWordDTO;
import com.pulmuone.demo.api.search.dto.SynonymDTO;
import com.pulmuone.demo.api.search.dto.UserWordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface SearchAdminMapper {

    List<CategoryBoostDomain> selectCategoryBoostList(@Param("keyword") String keyword);
    int updateCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO);
    int deleteCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO);
    int insertCategoryBoostScore(CategoryBoostScoreDTO categoryBoostScoreDTO);
    List<SynonymDomain> selectSynonymList(@Param("keyword") String keyword);
    int updateSynonym(SynonymDTO synonymDTO);
    int insertSynonym(SynonymDTO synonymDTO);
    int deleteSynonym(SynonymDTO synonymDTO);
    List<SynonymDomain>  selectAllSynonymList();
    List<UserDictionaryDomain> selectAllUserWordList();

    List<UserDictionaryDomain> selectUserWordList(@Param("keyword") String keyword);

    int updateUserWord(UserWordDTO userWordDTO);

    int insertUserWord(UserWordDTO userWordDTO);

    int deleteUserWord(UserWordDTO userWordDTO);

    List<StopWordDictionaryDomain> selectStopWordList(String keyword);

    int updateStopWord(StopWordDTO stopWordDTO);

    int insertStopWord(StopWordDTO stopWordDTO);

    int deleteStopWord(StopWordDTO stopWordDTO);

    List<StopWordDictionaryDomain> selectAllStopWordList();
}
