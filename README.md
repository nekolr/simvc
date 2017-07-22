# simvc
Simple MVC Framework    
 
这是一个仿Spring MVC风格的MVC框架，有轮子了为什么还要造轮子？纯粹为了实现之前的一个小小的愿望，自己写一个MVC框架。

## 使用示例
```java
@Controller
@RequestMapping(value = "/")
public class ZtreeController {

    @RequestMapping(value = "/index.do", method = RequestMethod.GET)
    public String index(){
        return "index";
    }
}
```
