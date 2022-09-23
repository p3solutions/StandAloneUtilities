package com.p3.tmb.sftp;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class sftpFileConnection {

	private String REMOTE_HOST = "1.2.3.4";
	private String USERNAME = "";
	private String PASSWORD = "";
	private int REMOTE_PORT;
	private int SESSION_TIMEOUT = 50000;
	private int CHANNEL_TIMEOUT = 5000;
	private String LOCAL_FILE_PATH;
	private String REMOTE_FILE_PATH;
	private Session jschSession = null;
	private Channel sftp  = null;
	private ChannelSftp channelSftp = null;
	private BufferedReader br = null;

	final Logger log = LogManager.getLogger(sftpFileConnection.class.getName());


	public sftpFileConnection(sftpBean sftpBean) {
		this.USERNAME = sftpBean.getUserName();
		this.PASSWORD = sftpBean.getPassword();
		this.REMOTE_HOST = sftpBean.getRemoteHost();
		this.REMOTE_PORT = Integer.parseInt(sftpBean.getRemotePort());
		this.LOCAL_FILE_PATH = sftpBean.getLocalPath();
		this.REMOTE_FILE_PATH = sftpBean.getRemotePath();
	}

//	public static void main(String[] args) throws IOException {
//		sftpBean sftpBean = new sftpBean();
//		sftpBean.setUserName("ubuntu");
//		sftpBean.setPassword("admin@123");
//		sftpBean.setRemoteHost("192.168.1.96");
//		sftpBean.setRemotePort("22");
//		sftpFileConnection sftp = new sftpFileConnection(sftpBean);
//		sftp.fetchFile();
//	}
	
	public ChannelSftp getChannelSftp() throws JSchException {
			 JSch jsch = new JSch();
			 
	            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
	            Properties config = new Properties();
	            config.put("StrictHostKeyChecking","no");
	            config.put("PreferredAuthentications", "password");
	            jschSession.setConfig(config);
	            jschSession.setPassword(PASSWORD);
	            jschSession.connect(SESSION_TIMEOUT);
	            
	           // jschSession.connect();

	            sftp = jschSession.openChannel("sftp");
	            sftp.connect();

	            // 5 seconds timeout
	           // sftp.connect(CHANNEL_TIMEOUT);

	            channelSftp = (ChannelSftp) sftp;
		return channelSftp;
	}
	
	public void closeConnections() throws IOException {
		if(channelSftp!=null) {
			channelSftp.exit();
			channelSftp.disconnect();
		}
		if (jschSession != null) {
          jschSession.disconnect();
      }		
		if(br != null){
			br.close();
		}
	}

	public BufferedReader getInputFileBufferReader() {
		
        try {
                br = new BufferedReader(new InputStreamReader(channelSftp.get(REMOTE_FILE_PATH)));

        } catch (Exception e) {

            e.printStackTrace();
            CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
        } 
        return br;
	}
public BufferedReader getFileBufferReader(String filePath) {
		
        try {
                br = new BufferedReader(new InputStreamReader(channelSftp.get(filePath)));

        } catch (Exception e) {

            e.printStackTrace();
            CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
        } 
        return br;
	}
	
	public boolean createFile(String filePath,String content) {
		try {
			channelSftp.put( new ByteArrayInputStream( content.getBytes() ), filePath);
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
	}
	public boolean uploadFiles(String inputFilePath,String uploadPath) {
		try {
			//channelSftp.get(inputDirectoryPath, downloadPath);
			channelSftp.put(inputFilePath, uploadPath);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			log.error("Error while transferring "+inputFilePath+" to sFTP server - Please refer Archon server for the file ");
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine + "Error while transferring "+inputFilePath+" to sFTP server - Please refer Archon server for the file ");
			return false;
		}
		return true;
		
	}
	
	public boolean createDir(String dirPath) {
		try {
			 try {
				 channelSftp.cd(dirPath);
		        }
		        catch ( SftpException e ) {
		        	channelSftp.mkdir(dirPath);
		        	channelSftp.cd(dirPath);
		        }
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
		
	}
	
	public boolean downloadFile(String remotePath) {
		try {
		channelSftp.cd(FileUtil.removeFileNameFromLinuxPath(remotePath));
		channelSftp.get(remotePath,LOCAL_FILE_PATH+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY);
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
	}
	
	public boolean deleteFile(String filePath) {
		try {
			channelSftp.cd(FileUtil.removeFileNameFromLinuxPath(filePath));
			channelSftp.rm(filePath);
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
	}
	
	
	public void downloadFromFolder(String folder) throws SftpException {
        Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(folder);
        log.info(entries);

        //download all from root folder
        for (ChannelSftp.LsEntry en : entries) {
            if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) 
            {
                continue;
            }

            log.info(en.getFilename());
            channelSftp.get(folder + en.getFilename(), en.getFilename());
        }
        
	}


	public void fetchFile() throws IOException {

		String localFile = "D:\\CodingRelated\\FleetCard\\input.txt";
		String remoteFile = "/home/ubuntu/tmbAutomation/input/";

		Session jschSession = null;

		try {

			JSch jsch = new JSch();
			// jsch.setKnownHosts("/home/mkyong/.ssh/known_hosts");
			jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);

			// authenticate using private key
//             jsch.addIdentity("/home/mkyong/.ssh/id_rsa");
			// jsch.addIdentity("");
			// authenticate using password
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications", "password");
			jschSession.setConfig(config);
			jschSession.setPassword(PASSWORD);

			// 10 seconds session timeout
			jschSession.connect(SESSION_TIMEOUT);

			Channel sftp = jschSession.openChannel("sftp");
			sftp.connect();

			// 5 seconds timeout
			// sftp.connect(CHANNEL_TIMEOUT);

			ChannelSftp channelSftp = (ChannelSftp) sftp;

			// transfer file from local to remote server
			channelSftp.put(localFile, remoteFile);

			// download file from remote server to local
//              InputStream is = channelSftp.get(remoteFile);
//              
//              try {
//                  BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                  // read from br
//                  String line;
//                  while ((line = br. readLine()) != null) { System. out. println(line);
//                  }
//
//              } finally {
//                  is.close();
//              }

			// log.info("Read File : \n" + readFile(is));

			channelSftp.exit();

		} catch (JSchException | SftpException e) {

			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);

		} finally {
			if (jschSession != null) {
				jschSession.disconnect();
			}
		}
		log.info("Done");

	}

	public String readFile(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line + System.lineSeparator());
		}

		return sb.toString();
	}
	
	public boolean checkForFileExistence(String path) {
	    Vector res = null;
	    try {
	        res = channelSftp.ls(path);
	    } catch (SftpException e) {
	        if (e.id == channelSftp.SSH_FX_NO_SUCH_FILE) {
	            return false;
	        }
	        //log.error("Unexpected exception during ls files on sftp: [{}:{}]", e.id, e.getMessage());
	    }
	    return res != null && !res.isEmpty();
	}

	public List<String> checkForTextFileExistence(String path) {
		List<String> textFiles = null;
		try {
			textFiles = new ArrayList<String>();
			channelSftp.cd(path);
			Vector<String> files = channelSftp.ls("*");
			for (int i = 0; i < files.size(); i++)
			{
			    Object obj = files.elementAt(i);
			    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry)
			    {
			        LsEntry entry = (LsEntry) obj;
			        if (true && !entry.getAttrs().isDir())
			        {
			        	if(entry.getFilename().endsWith(".TXT") || entry.getFilename().endsWith(".txt")) {
			        		textFiles.add(entry.getFilename());
			        	}
			        		
			        }
			    }
			}	
		} catch (Exception e) {
		
		}
		return textFiles;
	}
	
	
	public void downloadFromFolder1(String folder) throws SftpException {
		
		channelSftp.cd(folder);
		Vector<String> files = channelSftp.ls("*");
		for (int i = 0; i < files.size(); i++)
		{
		    Object obj = files.elementAt(i);
		    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry)
		    {
		        LsEntry entry = (LsEntry) obj;
		        if (true && !entry.getAttrs().isDir())
		        {
		        	String fileName = entry.getFilename();
		        	channelSftp.get(folder + "/" + fileName, LOCAL_FILE_PATH + File.separator + CommonSharedConstants.INPUT_BACKUP_DIRECTORY);
//		        	CommonSharedConstants.logContent.append("File downloaded to : " + (LOCAL_FILE_PATH + File.separator + CommonSharedConstants.INPUT_BACKUP_DIRECTORY + "\\" + fileName + CommonSharedConstants.newLine));
//		        	log.info("File downloaded to : " + (LOCAL_FILE_PATH + File.separator + CommonSharedConstants.INPUT_BACKUP_DIRECTORY+fileName));
//		        	if(!fileName.equals(CommonSharedConstants.folderProp))
//		        	channelSftp.rm(folder + "/" + fileName);
		           //ret.add(entry.getFilename());
		        }
//		        if (true && entry.getAttrs().isDir())
//		        {
//		            if (!entry.getFilename().equals(".") && !fileName.equals(".."))
//		            {
//		            	channelSftp.get(folder +"/"+ entry.getAttrs()+ "/" + entry.getFilename(), LOCAL_FILE_PATH + File.separator + "inputBakup");
//		                //ret.add(entry.getFilename());
//		            }
//		        }
		    }
		}		
	}
	


}
