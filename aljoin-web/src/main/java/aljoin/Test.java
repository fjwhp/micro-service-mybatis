package aljoin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {

	public static void main(String[] args) {
		BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		String pwd = bc.encode("123");
		System.out.println(pwd);
		// $2a$10$Qt/vrMWpZ6QcwwjTZmpHr.rYFPK2W8amvXrW1Y/tNW5W7oSJNV2/W
		// $2a$10$CqoZ.k5QUjEN33XECrgyxOYuqZLEM4o26M4i0ZmqPFDvAYg.MBBcy
		// $2a$10$I/FjOcezrVBP4rLCXTbLdepXtotMiXV6dYp3emh6Ih6PFdC2RKyQG
		// $2a$10$JnOWBetB.e9TgtvKtVMD8u07ZI7QmiGwY46pa5sIC1mx4QCwLQ4by
		System.out.println(bc.matches("123", "$2a$10$Qt/vrMWpZ6QcwwjTZmpHr.rYFPK2W8amvXrW1Y/tNW5W7oSJNV2/W"));
		System.out.println(bc.matches("123", "$2a$10$CqoZ.k5QUjEN33XECrgyxOYuqZLEM4o26M4i0ZmqPFDvAYg.MBBcy"));
		System.out.println(bc.matches("123", "$2a$10$I/FjOcezrVBP4rLCXTbLdepXtotMiXV6dYp3emh6Ih6PFdC2RKyQG"));
		System.out.println(bc.matches("123", "$2a$10$JnOWBetB.e9TgtvKtVMD8u07ZI7QmiGwY46pa5sIC1mx4QCwLQ4by"));
	}

}
