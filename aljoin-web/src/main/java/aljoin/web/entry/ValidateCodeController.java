package aljoin.web.entry;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 验证码控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
@RequestMapping("validateCode")
public class ValidateCodeController {
  private final static Logger logger = LoggerFactory.getLogger(ValidateCodeController.class);

	/**
	 * 
	 * 生成图片校验码
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年5月29日 下午4:51:49
	 */
	@RequestMapping("codeImg.html")
	public void codeImg(HttpServletRequest request, HttpServletResponse response) {
		try {
			Random r = new Random();
			response.addHeader("Pragma", "No-cache");
			response.addHeader("Cache-Control", "no-cache");
			response.addDateHeader("expires", 0);
			int width = 83;
			int height = 36;
			BufferedImage pic = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics gc = pic.getGraphics();
			gc.setColor(Color.BLACK);
			gc.fillRect(0, 0, width, height);
			String[] rNum = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
			gc.setColor(Color.WHITE);
			String rt = "";
			int x = 0;
			int y = 0;
			for (int i = 0; i < 5; i++) {
				String temp = rNum[r.nextInt(10)];
				rt = rt + temp;
				gc.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
				x = i * 17;
				if (i == 0) {
					x += 2;
				}
				y = 25;
				gc.drawString(temp, x, y);
			}
			gc.dispose();
			request.getSession().setAttribute("USER_LOGIN_SESSION_CODE", rt);
			OutputStream os = response.getOutputStream();
			ImageIO.write(pic, "PNG", os);
			os.flush();
			os.close();
		} catch (IOException e) {
		  logger.error("", e);
		}
	}
}
