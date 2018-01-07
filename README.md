# simvc   
Spring MVC风格的简易MVC框架，为实现自己的MVC框架提供一个参考。     

![simvc](https://i.loli.net/2018/01/07/5a520a1376346.png)
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
