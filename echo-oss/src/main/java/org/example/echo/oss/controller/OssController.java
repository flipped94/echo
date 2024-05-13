package org.example.echo.oss.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.echo.oss.service.OssService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/oss")
public class OssController {

    @Resource
    private OssService ossService;

    @PostMapping("/{bucketName}")
    public Result<Boolean> createBucket(@PathVariable("bucketName") String bucketName) {
        boolean success = ossService.create(bucketName);
        return Result.success(success);
    }

    @PostMapping("/upload/{bucketName}")
    public Result<String> upload(@PathVariable("bucketName") String bucketName, @RequestParam("file") MultipartFile file) {
        String url = ossService.upload(file, bucketName);
        return Result.success(url);
    }

    @DeleteMapping("/{bucketName}/{fileName}")
    public Result<Void> delete(@PathVariable("bucketName") String bucketName, @PathVariable("fileName") String fileName) {
        ossService.delete(bucketName, fileName);
        return Result.success(null);
    }

}
