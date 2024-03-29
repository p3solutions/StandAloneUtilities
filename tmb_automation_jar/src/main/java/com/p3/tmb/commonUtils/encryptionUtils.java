package com.p3.tmb.commonUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;

public class encryptionUtils {

	final Logger log = LogManager.getLogger(encryptionUtils.class.getName());
	private static String encryptionPassword = "UEFUSUVOVDNTSVhUWSAocDM2MCk=";
		private static String encryptionAlgorithm = "PBEWithMD5AndDES";
		
//		public static void main(String[] args) {
//			String password = "Secret@123";
//			log.info(new EncryptionUtils().encryptor(password));
//			}

		public static String decryptor(String encryptedValue) {
			String decryptedValue = "";
			if (encryptedValue.matches("ENC\\((.*)\\)")) {
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
				decryptor.setPassword(encryptionPassword);
				decryptor.setAlgorithm(encryptionAlgorithm);
				decryptor.setIvGenerator(new NoIvGenerator());
				decryptedValue = decryptor.decrypt(encryptedValue.replace("ENC(", "").replace(")", ""));
			} else
				decryptedValue = encryptedValue;
			return decryptedValue;
		}

		public static String encryptor(String strToEncrypt) {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(encryptionPassword);
			encryptor.setAlgorithm(encryptionAlgorithm);
			encryptor.setIvGenerator(new NoIvGenerator());
			return encryptor.encrypt(strToEncrypt);
		}
}

