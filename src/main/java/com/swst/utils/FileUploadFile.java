package com.swst.utils;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class FileUploadFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private File file;// �ļ�
	private String file_md5;// �ļ���
	private int starPos;// ��ʼλ��
	private byte[] bytes;// �ļ��ֽ�����
	private int endPos;// ��βλ��
}
