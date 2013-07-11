/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.utility;

import java.io.File;
import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

/**
 * @author Juan Pablo Fernandez Ramirez
 * Calculates a SHA-1 or MD5 digest for a file
 * 
 * @version $Rev$ $Date$
 */
public class FileDigest {
	
	/**
	 * Gets file content 
	 * @param file	File to read
	 * @return	a <code>byte[]</code> with the file content
	 * @throws	RuntimeException if a problem happens
	 */
	private static byte[] getFileContent(File file) {
		try {
			if (file.isDirectory()) {
				throw new RuntimeException(file.getAbsolutePath() + " must be a file not a directory");
			}
			return (FileUtils.readFileToByteArray(file));
		}
		catch (IOException ioE) {
			throw new RuntimeException(ioE.getMessage());
		}
	} //- getFileContent
	
	/**
	 * Calculates the MD5 digest of the file an d returns the value as a 16 element
	 * <code>byte[]</code>.
	 * @param toDigest	Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(File toDigest) {
		byte[] fileContent = getFileContent(toDigest);
		return DigestUtils.md5(fileContent);
	}
	
	/**
	 * Calculates the MD5 digest of the file and returns the value as a 16 element
	 * <code>byte[]</code>.
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return MD5 digest
	 */
	public static byte[] md5(String filePath, String fileName){
		File data = new File(filePath, fileName);
		return md5(data);
	} //- md5
	
	/**
	 * Calculates a file's MD5 digest and returns it as a 32 character hex string
	 * @param toDigest	Data to digest	
	 * @return	MD5 digest as a HEX string
	 */
	public static String md5Hex(File toDigest){
		byte[] fileContent = getFileContent(toDigest);
		return DigestUtils.md5Hex(fileContent);
	} //- md5Hex
	
	/**
	 * Calculates a file's MD5 digest and returns it as a 32 character hex string
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return	MD5 digest as a HEX string
	 */
	public static String md5Hex(String filePath, String fileName){
		File data = new File(filePath, fileName);
		return md5Hex(data);
	} //- md5Hex
	
	/**
	 * Calculates a file's SHA1 digest and returns it as a <code>byte[]</code>.
	 * @param toDigest	Data to digest
	 * @return	SHA1 digest
	 */
	public static byte[] sha1(File toDigest){
		byte[] fileContent = getFileContent(toDigest);
		return DigestUtils.sha(fileContent);
	} //- sha1
	
	/**
	 * Calculates a file's SHA-1 digest and returns it as a <code>byte[]</code>.
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return	SHA-1 digest
	 */
	public static byte[] sha1(String filePath, String fileName){
		File data = new File(filePath, fileName);
		return sha1(data);
	} //- sha1
	
	/**
	 * Calculates a file's SHA-1 digest and returns it as a 32 character hex string
	 * @param toDigest
	 * @return SHA-1 digest as a hex string
	 */
	public static String sha1Hex(File toDigest){
		byte[] fileContent = getFileContent(toDigest);
		return DigestUtils.shaHex(fileContent);
	} //- sha1Hex
	
	/**
	 * Calculates a file's SHA-1 digest and returns it as a 32 character hex string
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return	SHA-1 digest as a hex string
	 */
	public static String sha1Hex(String filePath, String fileName){
		File data = new File(filePath, fileName);
		return sha1Hex(data);
	} //- sha1Hex
	
	/**
	 * Informs if the MD5 file digest is equals to a given digest
	 * @param digest A string representing a file digest in hex
	 * @param toValidate File to validate againts digest
	 * @return true if the MD5 file digest is equals to digest
	 */
	public static boolean validateMD5(String digest, File toValidate) {
		String fileDigest = md5Hex(toValidate);
		return (fileDigest.equals(digest));
	} //- validateMD5
	
	/**
	 * Informs if the MD5 file digest is equals to a given digest
	 * @param digest 	digest A string representing a file digest in hex
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return true if the MD5 file digest is equals to digest
	 */
	public static boolean validateMD5(String digest, String filePath, String fileName) {
		String fileDigest = md5Hex(new File(filePath, fileName));
		return (fileDigest.equals(digest));
	} //- validateMD5
	
	/**
	 * Informs if the MD5 file digest is equals to a given digest
	 * @param digest  a <code>byte[]</code> that represents a file digest
	 * @param toValidate File to validate againts digest
	 * @return	true if the MD5 file digest is equals to digest
	 */
	public static boolean validateMD5(byte[] digest, File toValidate) {
		String fileDigest = new String(Hex.encodeHex(digest));
		return validateMD5(fileDigest, toValidate);
	} //- validateMD5
	
	/**
	 * Informs if the MD5 file digest is equals to a given digest
	 * @param digest 	digest  a <code>byte[]</code> that represents a file digest
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return true if the MD5 file digest is equals to digest
	 */
	public static boolean validateMD5(byte[] digest, String filePath, String fileName) {
		String fileDigest = new String(Hex.encodeHex(digest));
		return validateMD5(fileDigest, new File(filePath, fileName));
	} //- validateMD5
	
	/**
	 * Informs if the SHA-1 file digest is equals to a given digest
	 * @param digest		A string representing a file digest in hex
	 * @param toValidate 	File to validate againts digest
	 * @return true if the SHA-1 file digest is equals to digest
	 */
	public static boolean validateSHA1(String digest, File toValidate) {
		String fileDigest = sha1Hex(toValidate);
		return (fileDigest.equals(digest));
	} //- validateSHA1
	
	/**
	 * Informs if the SHA-1 file digest is equals to a given digest
	 * @param digest	A string representing a file digest in hex
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return true if the SHA-1 file digest is equals to digest
	 */
	public static boolean validateSHA1(String digest, String filePath, 
			String fileName) {
		String fileDigest = sha1Hex(new File(filePath, fileName));
		return (fileDigest.equals(digest));
	} //- validateSHA1
	
	/**
	 * Informs if the SHA-1 file digest is equals to a given digest
	 * @param digest  		a <code>byte[]</code> that represents a file digest
	 * @param toValidate 	File to validate againts digest
	 * @return	true if the SHA-1 file digest is equals to digest
	 */
	public static boolean validateSHA1(byte[] digest, File toValidate) {
		String fileDigest = new String(Hex.encodeHex(digest));
		return validateSHA1(fileDigest, toValidate);
	} //- validateSHA1
	
	/**
	 * Informs if the SHA-1 file digest is equals to a given digest
	 * @param digest	digest  a <code>byte[]</code> that represents a file digest
	 * @param filePath	Parent abstract pathname
	 * @param fileName	Child path name
	 * @return	true if the SHA-1 file digest is equals to digest
	 */
	public static boolean validateSHA1(byte[] digest, String filePath, 
			String fileName) {
		String fileDigest = new String(Hex.encodeHex(digest));
		return validateSHA1(fileDigest, new File(filePath, fileName));
	} //- validateSHA1
} //- class FileDigest