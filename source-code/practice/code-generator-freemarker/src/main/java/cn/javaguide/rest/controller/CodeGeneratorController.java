package cn.javaguide.rest.controller;

import cn.javaguide.service.CodeGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shuang.kou
 * @createTime 2020年12月31日 14:03:00
 **/
@RestController
@RequiredArgsConstructor
public class CodeGeneratorController {
    private final CodeGeneratorService codeGeneratorService;
}
