package org.example.echo.article.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.PageResult;
import org.example.common.vo.Result;
import org.example.echo.article.domain.ArticleForPublish;
import org.example.echo.article.domain.ArticleForSave;
import org.example.echo.article.domain.ListReq;
import org.example.echo.article.enums.ArticleStatusEnum;
import org.example.echo.article.service.ArticleService;
import org.example.echo.mvcconfig.LoginUserContext;
import org.example.echo.sdk.article.ArticleVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 发布文章
     *
     * @param articleForPublish 文章
     * @return 文章id
     */
    @PostMapping(value = "/publish")
    public Result<String> publish(@RequestBody @Valid ArticleForPublish articleForPublish) {
        Long articleId = articleService.publish(articleForPublish);
        return Result.success(articleId + "");
    }

    /**
     * 创作者新建或修改
     *
     * @return 文章id
     */
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody @Valid ArticleForSave articleForSave) {
        Long articleId = articleService.save(articleForSave);
        return Result.success(articleId + "");
    }

    /**
     * 设置为仅自己可见
     *
     * @param id 文章id
     * @return 设置是否成功
     */
    @GetMapping(value = "/withdraw/{id}")
    public Result<Boolean> withdraw(@PathVariable("id") Long id) {
        boolean result = articleService.syncStatus(id, ArticleStatusEnum.PRIVATE.getCode());
        return Result.success(result);
    }

    /**
     * 创作者中心已发布列表
     *
     * @return 已发表列表
     */
    @GetMapping("/list")
    public Result<PageResult<ArticleVO>> pubList(ListReq req) {
        PageResult<ArticleVO> result = articleService.listByAuthor(req, LoginUserContext.getUserId());
        return Result.success(result);
    }

    /**
     * 创作者文章详情
     *
     * @param id 文章id
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        ArticleVO articleVO = articleService.getByIdAndAuthorId(id, LoginUserContext.getUserId());
        return Result.success(articleVO);
    }

    /**
     * 读者文章详情
     *
     * @param id 文章id
     * @return 文章详情
     */
    @LoginNotRequired
    @GetMapping(value = "/pub/{id}")
    public Result<ArticleVO> pubDetail(@PathVariable("id") Long id) {
        ArticleVO articleVO = articleService.getPubById(id);
        return Result.success(articleVO);
    }

    /**
     * 读者
     *
     * @return 已发表列表
     */
    @LoginNotRequired
    @GetMapping("/pub/list")
    public Result<PageResult<ArticleVO>> readerPubList(ListReq req) {
        PageResult<ArticleVO> result = articleService.readerPubList(req);
        return Result.success(result);
    }

    @LoginNotRequired
    @GetMapping("/listpub")
    public Result<List<ArticleVO>> listPub(@RequestParam("time") long time,
                                           @RequestParam("days") long days,
                                           @RequestParam("offset") long offset,
                                           @RequestParam("batchSize") long batchSize) {
        List<ArticleVO> res = articleService.listPub(time, days, offset, batchSize);
        return Result.success(res);
    }
}
