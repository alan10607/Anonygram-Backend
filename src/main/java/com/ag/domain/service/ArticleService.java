package com.ag.domain.service;

import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.constant.StatusType;
import com.ag.domain.model.Article;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.util.ObjectFieldUtil;
import com.ag.domain.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService extends CRUDServiceImpl<ArticleDTO> {
    private final ArticleRepository articleRepository;

    public ArticleDTO get(String id, int no) {
        return this.get(new ArticleDTO(id, no));
    }

    public ArticleDTO delete(String id, int no) {
        return this.delete(new ArticleDTO(id, no));
    }

    public ArticleDTO patchWord(ArticleDTO articleDTO) {
        ArticleDTO patchDTO = ObjectFieldUtil.retainFields(articleDTO, "id", "no", "word");
        return this.patch(patchDTO);
    }

    public ArticleDTO patchStatus(ArticleDTO articleDTO) {
        ArticleDTO patchDTO = ObjectFieldUtil.retainFields(articleDTO, "id", "no", "status");
        return this.patch(patchDTO);
    }

//    public ArticleDTO patchLike(ArticleDTO articleDTO) {
//        ArticleDTO patchDTO = this.get(articleDTO.getId(), articleDTO.getNo());
//        patchDTO.setLikes(patchDTO.getLikes() + 1);
//        //TODO: need work to impl like service
//        return this.patch(patchDTO);
//    }

    //    public List<String> getId(){
//        return idService.get();
//    }
//
//    public List<ArticleDTO> getArticleWithContent(List<String> idList, List<Integer> noList){
//        return idList.stream().map(id -> getArticleWithContent(id, noList)).collect(Collectors.toList());
//    }
//
//    public ArticleDTO getArticleWithContent(String id, int no){
//        return getArticleWithContent(id, Collections.singletonList(no));
//    }
//
//    public ArticleDTO getArticleWithContent(String id, List<Integer> noList){
//        ArticleDTO articleDTO = articleService.get(id);
//        if(articleDTO.getStatus() == StatusType.NORMAL) {
//            articleDTO.setContentList(noList.stream()
//                    .map(no -> contentService.get(id, no))
//                    .collect(Collectors.toList()));
//        }else{
//            articleDTO.setContentList(noList.stream()
//                    .map(no -> new ContentDTO(id, no, articleDTO.getStatus()))
//                    .collect(Collectors.toList()));
//        }
//        return articleDTO;
//    }
//
//    public void checkArticleStatusIsNormal(String id){
//        ArticleDTO articleDTO = articleService.get(id);
//        if(articleDTO.getStatus() != StatusType.NORMAL){
//            throw new AnonygramIllegalStateException("Article status of this content is {} normal, id={}",
//                    articleDTO.getStatus(), id);
//        }
//    }
//
//    public void checkContentStatusIsNormal(String id, int no){
//        checkArticleStatusIsNormal(id);
//        ContentDTO contentDTO = contentService.get(id, no);
//        if(contentDTO.getStatus() != StatusType.NORMAL){
//            throw new AnonygramIllegalStateException("Content status is {}, id={}, no={}",
//                    contentDTO.getStatus(), id, no);
//        }
//    }

    @Override
    public ArticleDTO getImpl(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public ArticleDTO createImpl(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public ArticleDTO updateImpl(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public ArticleDTO patchImpl(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public ArticleDTO deleteImpl(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    protected void validateCreate(ArticleDTO articleDTO) {
        //TODO: prepareCreateEntity, if no=null, no=0
        validateTitle(articleDTO);
        validateWord(articleDTO);
    }

    protected void validateUpdateAndPatch(ArticleDTO articleDTO) {
        //TODO: preparePatchEntity?
        validateWord(articleDTO);
        validateStatus(articleDTO);
    }

    protected void validateDelete(ArticleDTO articleDTO) {
        validateStatus(articleDTO);
    }

    private void validateWord(ArticleDTO articleDTO) {
        //TODO:if no =0 thenword not blank and size
    }

    private void validateTitle(ArticleDTO articleDTO) {
        //TODO:word not blank and size
    }

    private void validateStatus(ArticleDTO articleDTO) {
        //TODO:check status is legal
    }

    private void validateIsExist(ArticleDTO articleDTO) {
        //TODO:must exist
    }

    private void validateIsNotExist(ArticleDTO articleDTO) {
        //TODO:must not exist
    }



}