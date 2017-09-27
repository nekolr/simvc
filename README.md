# simvc   
这是一个仿Spring MVC风格的MVC框架，有轮子了为什么还要造轮子？emmm，就是闲的 :joy:

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
