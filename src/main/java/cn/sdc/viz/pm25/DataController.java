package cn.sdc.viz.pm25;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class DataController {

	@RequestMapping(value="pm25/data.do")
	public @ResponseBody String data(@RequestParam String key){
		String v = new Main(key).task().toString();
		return v;
	}
	
}
