package search.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;

@Controller
@CrossOrigin
public class MyInfoController {

	private static Logger logger = LogManager.getLogger(MyInfoController.class);

	@GetMapping(value = "/about-me")
	public String myInfo() {

		logger.info("RESUME");
		return "resumePage";
	}

	@GetMapping(value = "/pdf")
	public ResponseEntity<byte[]> pdfDownload(
			@RequestParam(value = "type", required = true) String type,
			HttpServletRequest httpServletRequest) throws Exception{

		byte[] resumeFile = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		if(type.equals("ch")) {
			logger.info("RESUME_CH");
			resumeFile = Files.readAllBytes(new File("pdf/ch.pdf").toPath());
		} else if(type.equals("en")) {
			logger.info("RESUME_EN");
			resumeFile = Files.readAllBytes(new File("pdf/en.pdf").toPath());
		} else {
			throw new RuntimeException();
		}
		return new ResponseEntity<byte[]>(resumeFile, headers, HttpStatus.OK);
	}
}
