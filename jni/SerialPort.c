/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*my name is cosmo*/
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>

#include "SerialPort.h"
//#include "libsynoalg.h"
//#include "add_minus.h"
#include "android/log.h"
static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

char LRC(const char *data,unsigned short len)
{
	int i = 0;
	char lrc = 0;
	while (len--){
		lrc ^= data[i++];
	}
	return lrc;
}

static void set_link(unsigned char *p_cmd, int m)
{
	LOGD("in set_link");
	unsigned char crc = 0x00;
	int i = 0;
	*(p_cmd + 7) = m;

	for (i = 0; i < 6; i++) {
		crc = crc ^ (*(p_cmd + i + 2));
	}
	*(p_cmd + 8) = crc;
}

static void set_q_value(unsigned char *p_cmd, int m)
{
	LOGD("in set_q_value");
	unsigned char crc = 0x00;
	int i = 0;
	*(p_cmd + 6) = m << 4;

	for (i = 0; i < 11; i++) {
		crc = crc ^ (*(p_cmd + i + 2));
	}
	*(p_cmd + 13) = crc;
}

static void set_power(unsigned char *p_cmd, int m)
{
	LOGD("in set_power");
	unsigned char crc = 0x00;
	int i = 0;
	m *= 100;
	*(p_cmd + 7) = *(p_cmd + 9) = m >> 8;
	*(p_cmd + 8) = *(p_cmd + 10) = m & 0xFF;

	for (i = 0; i < 9; i++) {
		crc = crc ^ (*(p_cmd + i + 2));
	}
	*(p_cmd + 11) = crc;
}

static char * cash_get_status(int m)
{
	switch (m) {
	case 0xE0:
		return "BD|1999";
	case 0xE1:
		return "BD|2005";
	case 0xA0:
		return "BU|1999";
	case 0xA1:
		return "BU|2005";
	case 0xC0:
		return "FD|1999";
	case 0xC1:
		return "FD|2005";
	case 0x80:
		return "FU|1999";
	case 0x81:
		return "FU|2005";
	default:
		return "xx|xxxx";
	}
}

static char * cash_getmoney(int n) {
	switch (n) {
	case 0x00:
		return "1";
	case 0x01:
		return "2";
	case 0x02:
		return "5";
	case 0x03:
		return "10";
	case 0x04:
		return "20";
	case 0x05:
		return "50";
	case 0x06:
		return "100";
	default:
		return "xxx";
	}
}

static speed_t getBaudrate(jint baudrate) {
	switch (baudrate) {
	case 0:
		return B0;
	case 50:
		return B50;
	case 75:
		return B75;
	case 110:
		return B110;
	case 134:
		return B134;
	case 150:
		return B150;
	case 200:
		return B200;
	case 300:
		return B300;
	case 600:
		return B600;
	case 1200:
		return B1200;
	case 1800:
		return B1800;
	case 2400:
		return B2400;
	case 4800:
		return B4800;
	case 9600:
		return B9600;
	case 19200:
		return B19200;
	case 38400:
		return B38400;
	case 57600:
		return B57600;
	case 115200:
		return B115200;
	case 230400:
		return B230400;
	case 460800:
		return B460800;
	case 500000:
		return B500000;
	case 576000:
		return B576000;
	case 921600:
		return B921600;
	case 1000000:
		return B1000000;
	case 1152000:
		return B1152000;
	case 1500000:
		return B1500000;
	case 2000000:
		return B2000000;
	case 2500000:
		return B2500000;
	case 3000000:
		return B3000000;
	case 3500000:
		return B3500000;
	case 4000000:
		return B4000000;
	default:
		return -1;
	}
}

/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open(
		JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags) {
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	LOGD("new.........................");
	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
		LOGD("****************open baudrate : %d",baudrate);
	}

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		LOGD("Opening serial port %s with flags 0x%x",
				path_utf, O_RDWR | flags);
		fd = open(path_utf, O_RDWR | flags);
		LOGD("open() fd = %d", fd);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		(*env)->DeleteLocalRef(env, path);
		if (fd == -1) {
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg)) {
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
		LOGE("zj xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);
		cfg.c_cflag &= ~CSTOPB;
		cfg.c_cflag &= ~PARENB;
		cfg.c_cflag &= ~CSIZE;
		cfg.c_cflag |= CS8;
		if (tcsetattr(fd, TCSANOW, &cfg)) {
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = (*env)->FindClass(env,
				"java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor,
				"<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor,
				"descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor,
				iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);
	}
	return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
(JNIEnv *env, jobject thiz)
{
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
close(descriptor);
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    write
 * Signature: (String)V
 */
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_write(
	JNIEnv *env, jobject thiz, jstring buf) {
int retval;
fd_set rfds;
struct timeval tv;
int sret;
int cmd_ret;
int i = 0;
int m = 0;
int x_3 = 0;
int n_3 = 0;
int n_x = 0;
int x_error = 0;
int head_not_body = 1;
int first = 1;
int second = 0;
int x_n = 0;
int r_len = 0;
int what_kinda_rfid;
char ret_buf[20];
const char *write_buf = NULL;
unsigned char final[12];
unsigned char crc = 0x00;
unsigned char ret[9];
unsigned char element[1];
unsigned char write_ret[3];
unsigned char writecode[43] = { 0xA5, 0x5A, 0x00, 0x2B, 0x86, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A };
char R2000repeat[10] = { 0xA5, 0x5A, 0x00, 0x0A, 0x82, 0x00, 0x00, 0x88, 0x0D,
		0x0A };
char R2000stop[8] = { 0xA5, 0x5A, 0x00, 0x08, 0x8C, 0x84, 0x0D, 0x0A };
char scanopen1[9] = { 0x07, 0xc6, 0x04, 0x00, 0xff, 0x8a, 0x08, 0xfd, 0x9e };
char scanopen2[6] = { 0x04, 0xe4, 0x04, 0x00, 0xff, 0x14 };

memset(final, 0x00, 12);
memset(ret, 0x00, 9);
memset(write_ret, '\0', 3);
write_buf = (*env)->GetStringUTFChars(env, buf, 0);
m = strlen(write_buf);

//memset(ret_buf, '\0', 20);
jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 1;
tv.tv_usec = 0;

if (!strcmp(write_buf, "2000repeat")) {
	tcflush(descriptor, TCIOFLUSH);
	cmd_ret = write(descriptor, R2000repeat, 10);
	LOGD("2000repeat successful");
	(*env)->ReleaseStringUTFChars(env, buf, write_buf);
	(*env)->DeleteLocalRef(env, buf);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	if (cmd_ret != 10)
		return 0;
	return 1;
}
if (!strcmp(write_buf, "2000stop")) {
	cmd_ret = write(descriptor, R2000stop, 8);
	LOGD("2000stop successful = %d", cmd_ret);
	tcflush(descriptor, TCIOFLUSH);
	(*env)->ReleaseStringUTFChars(env, buf, write_buf);
	(*env)->DeleteLocalRef(env, buf);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	if (cmd_ret != 8)
		return 0;
	return 1;
}
if (!strcmp(write_buf, "scansetpara")) {
	cmd_ret = write(descriptor, scanopen1, 9);
	LOGD("new scanopen1 successful = %d", cmd_ret);
	(*env)->ReleaseStringUTFChars(env, buf, write_buf);
	LOGD("11111111111111111111111");
	(*env)->DeleteLocalRef(env, buf);
	LOGD("11111111111111111111111");
	(*env)->DeleteLocalRef(env,SerialPortClass);
	LOGD("11111111111111111111111");
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	LOGD("11111111111111111111111");
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	LOGD("22222222222222222222222");
	(*env)->DeleteLocalRef(env,mFd);
	LOGD("fdsafdsfdsf");
	if (cmd_ret != 9)
		return 0;
	return 1;
}
if (!strcmp(write_buf, "scanopen")) {
	tcflush(descriptor, TCIOFLUSH);
	cmd_ret = write(descriptor, scanopen2, 6);
	LOGD("new scanopen2 successful = %d", cmd_ret);
	(*env)->ReleaseStringUTFChars(env, buf, write_buf);
	(*env)->DeleteLocalRef(env, buf);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	if (cmd_ret != 6)
		return 0;
	return 1;
}

LOGD("1.1");
for (i = 0; i < 24; i = i + 2) {
	if (i < (m)) {
		if ((write_buf[i] > 0x2F) && (write_buf[i] < 0x3A)) {
			final[i / 2] = (write_buf[i] - 0x30) << 4;
		} else if ((write_buf[i] > 0x40) && (write_buf[i] < 0x47)) {
			final[i / 2] = (write_buf[i] - 0x37) << 4;
		} else if ((write_buf[i] > 0x60) && (write_buf[i] < 0x67)) {
			final[i / 2] = (write_buf[i] - 0x57) << 4;
		} else {
			//printf("other\n");
		}
		if ((write_buf[i + 1] > 0x2F) && (write_buf[i + 1] < 0x3A)) {
			final[i / 2] += write_buf[i + 1] - 0x30;
		} else if ((write_buf[i + 1] > 0x40) && (write_buf[i + 1] < 0x47)) {
			final[i / 2] += write_buf[i + 1] - 0x37;
		} else if ((write_buf[i + 1] > 0x60) && (write_buf[i + 1] < 0x67)) {
			final[i / 2] += write_buf[i + 1] - 0x57;
		} else {
			//printf("other\n");
		}
	}
}
LOGD("1.2");
for (i = 0; i < 12; i++) {
	writecode[i + 28] = final[i];
	printf("0x%x\n", final[i]);
}
/* crc */
for (i = 0; i < 38; i++) {
	crc = crc ^ writecode[i + 2];
}
writecode[40] = crc;

LOGD("1.3");
cmd_ret = write(descriptor, writecode, 43);
LOGD("write ret = %d", cmd_ret);
LOGD("descriptor = %d", descriptor);
LOGD("1.4");

while(1){
	FD_ZERO(&rfds);
	FD_SET(descriptor, &rfds);
	sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
	if(sret < 0){
		LOGD("new select error!");
		return -1;
	}else if(sret == 0){
		LOGD("<write timeout>");
		return -1;
	}else{
		if(head_not_body){
			/* head */
			read(descriptor, element, 1);
			if(first && (*element == 0xA5)){
				LOGD("write 1: 0x%x", *element);
				first = 0;
				second = 1;
				continue;
			}else if(first){
				x_error++;
				if(x_error == 50){
					return -1;
				}
				continue;
			}
			if(second && (*element == 0x5A)){
				LOGD("write 2: 0x%x", *element);
				first = 0;
				second = 0;
				continue;
			}else if(second){
				x_error++;
				first = 1;
				second = 0;
				continue;
			}
			n_3++;
			if(n_3 == 2){
				r_len = *element - 5;
				LOGD("length = %d", r_len);
				continue;
			}
			if((n_3 == 3) && *element == 0xFF){
				LOGD("module error!");
				return -1;
			}
			if((n_3 == 3) && (*element == 0x87)){
				LOGD("<<<<<0x87");
				x_3 = 0;
				head_not_body = 0;
				continue;
			}
			if((n_3 == 3) && (*element != 0x87)){
				LOGD("<<<<<not 0x87");
				x_3 = 0;
				return -1;
			}
		}else{
			/* body */
			LOGD("in body");
			read(descriptor, ret + n_x, 1);
			LOGD("body : 0x%x", ret[n_x]);
			n_x++;
			if(n_x == (r_len)){
				LOGD("write okey 18 = %d, process now", n_x);
				break;
			}
		}
	}
}
LOGD("write flag = 0x%x, code = 0x%x", ret[0], ret[1]);

(*env)->ReleaseStringUTFChars(env, buf, write_buf);
(*env)->DeleteLocalRef(env, buf);
(*env)->DeleteLocalRef(env,SerialPortClass);
(*env)->DeleteLocalRef(env,FileDescriptorClass);
//(*env)->DeleteLocalRef(env,mFdID);
//(*env)->DeleteLocalRef(env,descriptorID);
(*env)->DeleteLocalRef(env,mFd);
if (ret[0] == 0x00){
	LOGD(">>>>>>write error! ret 0");
	return 0;
}else if(ret[0] == 0x01){
	LOGD(">>>>>>write successful! ret 1");
	return 1;
}
}
/*
 * Class:     cedric_serial_SerialPort
 * Method:    getRFIDState 
 * Signature: (String)V
 */
JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_getRFIDState(
	JNIEnv *env, jobject thiz) {

fd_set rfds;
struct timeval tv;
int sret;
int retval;
int num, n_x, n_11, n_16;

unsigned char header[1];
unsigned char get_power[8] = { 0xA5, 0x5A, 0x00, 0x08, 0x12, 0x1A, 0x0D, 0x0A };
unsigned char get_link[10] = { 0xA5, 0x5A, 0x00, 0x0A, 0x54, 0x00, 0x00, 0x5E,
		0x0D, 0x0A };
unsigned char get_q_value[8] =
		{ 0xA5, 0x5A, 0x00, 0x08, 0x22, 0x2A, 0x0D, 0x0A };
int cmd_ret = 0;
unsigned char read_buf[20];
int i = 0;
char **p_set_name = NULL;
unsigned char final[8] = "get ok!\0";

memset(read_buf, 0xFF, 20);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);
tv.tv_sec = 4;
tv.tv_usec = 0;

sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
LOGD("select return %d", sret);
if (sret == -1) {
	LOGD("new select error!");
} else if (sret > 0) {

	LOGD("get 1");
	/* get power */
	tcflush(descriptor, TCIOFLUSH);
	cmd_ret = write(descriptor, get_power, 8);
	if (cmd_ret != 8) {
		LOGD("sending write power_cmd error!");
	}
	while (1) {
		read(descriptor, header, 1);
		if (header[0] == 0xA5) {
			read(descriptor, header, 1);
			if (header[0] == 0x5A) {
				read(descriptor, header, 1);
				read(descriptor, header, 1);
				num = header[0] - 3;
				break;
			}
		}
	}
	LOGD("get 2");
	while (1) {
		retval = read(descriptor, read_buf + n_x, num - n_x);
		n_x += retval;
		if (n_x == num)
			break;
	}

	LOGD("get 3");
	/* get link */
	memset(read_buf, 0xFF, 20);
	tcflush(descriptor, TCIOFLUSH);
	cmd_ret = write(descriptor, get_link, 10);
	if (cmd_ret != 10) {
		LOGD("sending write link_cmd error!");
	}
	while (1) {
		retval = read(descriptor, read_buf + n_11, 11 - n_11);
		n_11 += retval;
		if (n_11 == 11)
			break;
	}
	if (read_buf[5]) {
		LOGD("###################get link  = %d", read_buf[7]);
	}

	LOGD("get 4");
	/* get q_value */
	memset(read_buf, 0xFF, 20);
	tcflush(descriptor, TCIOFLUSH);
	cmd_ret = write(descriptor, get_q_value, 8);
	if (cmd_ret != 8) {
		LOGD("sending write q_value_cmd error!");
	}
	while (1) {
		retval = read(descriptor, read_buf + n_16, 16 - n_16);
		n_16 += retval;
		if (n_11 == 16)
			break;
	}
	LOGD("###################get q_value  = %d", (read_buf[6] >> 4));
} else {
	LOGD("get Parameter Read timeout");
}

return (*env)->NewStringUTF(env, final);
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    setParameters 
 * Signature: (String)V
 */
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_setParameter(
	JNIEnv *env, jobject thiz, jstring name, jint value) {
fd_set rfds;
struct timeval tv;
int sret;
int retval = 0;
int n_9 = 0;

char *set_name[3] = { "power", "q_value", "link" };
unsigned char power_cmd[14] = { 0xA5, 0x5A, 0x00, 0x0E, 0x10, 0x00, 0x01, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A };
unsigned char link_cmd[11] = { 0xA5, 0x5A, 0x00, 0x0B, 0x52, 0x00, 0x00, 0x00,
		0x00, 0x0D, 0x0A };
unsigned char q_value_cmd[16] = { 0xA5, 0x5A, 0x00, 0x10, 0x20, 0x10, 0x00,
		0xF4, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A };
int cmd_ret = 0;
unsigned char read_buf[20];
int i = 0;
char **p_set_name = NULL;
const char *write_code = (*env)->GetStringUTFChars(env, name, 0);
p_set_name = set_name;

memset(read_buf, 0xFF, 20);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 4;
tv.tv_usec = 0;

p_set_name = set_name;
while (1) {
	i++;
	if (!strcmp(write_code, *(p_set_name++))) {
		break;
	}
	if (i > 2) {
		i = 0;
		break;
	}
}
tcflush(descriptor, TCIOFLUSH);
switch (i) {
case 1:
	set_power(power_cmd, value);
	LOGD("Parameter: power");
	cmd_ret = write(descriptor, power_cmd, 14);
	if (cmd_ret != 14) {
		LOGD("sending write power_cmd error!");
	}
	LOGD("%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x",
			power_cmd[0], power_cmd[1], power_cmd[2], power_cmd[3], power_cmd[4], power_cmd[5], power_cmd[6], power_cmd[7], power_cmd[8], power_cmd[9], power_cmd[10], power_cmd[11], power_cmd[12], power_cmd[13]);
	break;
case 2:
	set_q_value(q_value_cmd, value);
	LOGD("Parameter: q_value");
	cmd_ret = write(descriptor, q_value_cmd, 16);
	if (cmd_ret != 16) {
		LOGD("sending write q_value_cmd error!");
	}
	break;
case 3:
	set_link(link_cmd, value);
	LOGD("Parameter: link ");
	cmd_ret = write(descriptor, link_cmd, 11);
	if (cmd_ret != 11) {
		LOGD("sending write link_cmd error!");
	}
	break;

case 0:
default:
	LOGD("Parameter not found");
}

sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
if (sret == -1) {
	(*env)->ReleaseStringUTFChars(env, name, write_code);
	(*env)->DeleteLocalRef(env, name);
	LOGD("new select error!");
} else if (sret > 0) {
	while (1) {
		retval = read(descriptor, read_buf + n_9, 9 - n_9);
		LOGD("3**********2=%d", n_9);
		LOGD("%x,%x,%x,%x,%x,%x,%x,%x,%x",
				read_buf[0], read_buf[1], read_buf[2], read_buf[3], read_buf[4], read_buf[5], read_buf[6], read_buf[7], read_buf[8]);
		n_9 += retval;
		if (n_9 > 7)
			break;
	}
	(*env)->ReleaseStringUTFChars(env, name, write_code);
	(*env)->DeleteLocalRef(env, name);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	if (read_buf[5]) {
		if (read_buf[4] == 0x11) {
			return 1;
		}
		if (read_buf[4] == 0x21) {
			return 1;
		}
		if (read_buf[4] == 0x53) {
			return 1;
		}
	}
} else {
	(*env)->ReleaseStringUTFChars(env, name, write_code);
	(*env)->DeleteLocalRef(env, name);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	//(*env)->DeleteLocalRef(env,mFdID);
	//(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	LOGD("set Parameter Read timeout");
}

LOGD("Parameter set error! (%x %x %x %x %x %x %x %x %x)",
		read_buf[0], read_buf[1], read_buf[2], read_buf[3], read_buf[4], read_buf[5], read_buf[6], read_buf[7], read_buf[8], read_buf[9]);
return 0;
}
/*
 * Class:     cedric_serial_SerialPort
 * Method:    setDefaultParameters 
 * Signature: (String)V
 */
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_setDefaultParameters(
	JNIEnv *env, jobject thiz) {
#if 1
int cmd_ret;
unsigned char r = 3;
unsigned char CHINA2[10] = { 0xA5, 0x5A, 0x00, 0x0A, 0x2C, 0x00, 0x02, 0x24,
		0x0D, 0x0A };
unsigned char RFLink[11] = { 0xA5, 0x5A, 0x00, 0x0B, 0x52, 0x00, 0x00, 0x02,
		0x5B, 0x0D, 0x0A };
unsigned char Genparameter[17] = { 0xA5, 0x5A, 0x00, 0x10, 0x20, 0x10, 0x30,
		0xF4, 0x04, 0x00, 0x00, 0x00, 0x00, 0xE0, 0x0D, 0x0A };

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

cmd_ret = write(descriptor, CHINA2, 10);
if (cmd_ret != 10)
	return r;
r--;
cmd_ret = write(descriptor, RFLink, 11);
if (cmd_ret != 11)
	return r;
r--;
cmd_ret = write(descriptor, Genparameter, 17);
if (cmd_ret != 17)
	return r;
r--;

#endif
return r;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    read
 * Signature: ()V
 */
JNIEXPORT jbyteArray JNICALL Java_android_1serialport_1api_SerialPort_wirelessRead(
	JNIEnv *env, jobject thiz) {
int retval;
char head[1];
char test_buf[300];
int read_ret = 0;
fd_set rfds;
struct timeval tv;
int sret;
int length;
int n_x = 0;
int i = 0;
int x_0 = 0;
int x_error = 0;
int head_not_body = 1;
int first = 1;
int second = 0;

jbyteArray bytearray;
jbyteArray backarray;
bytearray = (*env)->NewByteArray(env, 300);

memset(test_buf, '\0', 300);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 1;
tv.tv_usec = 0;

LOGD("wireless start........................");
while(1){
	FD_ZERO(&rfds);
	FD_SET(descriptor, &rfds);

	sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
	if(sret < 0){
		LOGD("new select error!");
		(*env)->DeleteLocalRef(env, bytearray);
		(*env)->DeleteLocalRef(env,SerialPortClass);
		(*env)->DeleteLocalRef(env,FileDescriptorClass);
		//(*env)->DeleteLocalRef(env,mFdID);
		//(*env)->DeleteLocalRef(env,descriptorID);
		(*env)->DeleteLocalRef(env,mFd);

		return NULL;
	}else if(sret == 0){
		/* timeout */
		LOGD("<read timeout>");
		(*env)->DeleteLocalRef(env, bytearray);
		(*env)->DeleteLocalRef(env,SerialPortClass);
		(*env)->DeleteLocalRef(env,FileDescriptorClass);
		//(*env)->DeleteLocalRef(env,mFdID);
		//(*env)->DeleteLocalRef(env,descriptorID);
		(*env)->DeleteLocalRef(env,mFd);
		return NULL;
	}else{
		if(head_not_body){
			/* head */
			read(descriptor, head, 1);
			if(first && (*head == 0x55)){
				LOGD("head 1 = 0x%x", *head);
				*test_buf = *head;
				first = 0;
				second = 1;
				continue;
			}else if(first){
				LOGD("not head1.......");
				x_error++;
				if(x_error == 50){
					LOGD("return now..........");
					(*env)->DeleteLocalRef(env, bytearray);
					(*env)->DeleteLocalRef(env,SerialPortClass);
					(*env)->DeleteLocalRef(env,FileDescriptorClass);
					//(*env)->DeleteLocalRef(env,mFdID);
					//(*env)->DeleteLocalRef(env,descriptorID);
					(*env)->DeleteLocalRef(env,mFd);
					return NULL;
				}
				continue;
			}
			if(second && (*head == 0x7A)){
				LOGD("head 2 = 0x%x", *head);
				*(test_buf + 1) = *head;
				first = 0;
				second = 0;
				continue;
			}else if(second){
				LOGD("not head 2.......");
				x_error++;
				first = 1;
				second = 0;
				continue;
			}
			*(test_buf + 2) = *head;
			length = *head;
			LOGD(">>>>>need to read length = %d",length);
			LOGD(">>>>>head is 0x%x  0x%x  0x%x", test_buf[0], test_buf[1], test_buf[2]);
			head_not_body = 0;
			continue;
		}else{
			/* body */
			LOGD(">>>>>>>>>>>>>>all read : %d", n_x);
			read(descriptor, test_buf + 3 + n_x, 1);
			n_x++;
			if(n_x == (length - 1))
				break;
		}
	}
}
LOGD("wireless end........................");

for(i = 0; i < (length + 2); i++){
	LOGD("read #%d: 0x%x", i, *(test_buf+i));
}
backarray = (*env)->NewByteArray(env, length+2);

(*env)->SetByteArrayRegion(env, backarray, 0, length + 2, test_buf);

(*env)->DeleteLocalRef(env, bytearray);
(*env)->DeleteLocalRef(env,SerialPortClass);
(*env)->DeleteLocalRef(env,FileDescriptorClass);
(*env)->DeleteLocalRef(env,mFdID);
(*env)->DeleteLocalRef(env,descriptorID);
(*env)->DeleteLocalRef(env,mFd);
return backarray;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    wireless_write
 * Signature: ()V
 */
JNIEXPORT int JNICALL Java_android_1serialport_1api_SerialPort_wirelessWrite(
	JNIEnv *env, jobject thiz, jbyteArray bytearray) {
int retval;
char *send_array;
int length;
char *s;
int i = 0;

jbyte *bytes = (*env)->GetByteArrayElements(env, bytearray, 0);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

s = (char *)bytes;
length = *(s + 2);
length += 2;
for(i = 0; i < length; i++){
	LOGD("write : 0x%x", *(s+i));
}
retval = write(descriptor, s, length);
if(retval != length){
	(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
	(*env)->DeleteLocalRef(env, bytearray);
	LOGD("wireless write length = %d", length);
	(*env)->DeleteLocalRef(env, bytes);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	(*env)->DeleteLocalRef(env,mFdID);
	(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);
	return 0;
}
(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
(*env)->DeleteLocalRef(env, bytearray);
LOGD("wireless write length = %d", length);
(*env)->DeleteLocalRef(env, bytes);
(*env)->DeleteLocalRef(env,SerialPortClass);
(*env)->DeleteLocalRef(env,FileDescriptorClass);
(*env)->DeleteLocalRef(env,mFdID);
(*env)->DeleteLocalRef(env,descriptorID);
(*env)->DeleteLocalRef(env,mFd);
return 1;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    r500_read
 * Signature: ()V
 */
JNIEXPORT jbyteArray JNICALL Java_android_1serialport_1api_SerialPort_r500read(
	JNIEnv *env, jobject thiz) {
int retval;
int i = 0;
int j = 0;
int n = 25;
char test_buf[25];
unsigned char x_bak[18];
unsigned char *h = NULL;
unsigned char *t = NULL;

jbyteArray bytearray;
bytearray = (*env)->NewByteArray(env, 18);

memset(test_buf, '\0', 25);
memset(x_bak, '\0', 18);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

/* READ */
read(descriptor, test_buf, sizeof(test_buf));
LOGD("read something %s", test_buf);

/* Change the value */
for (i = 0; i < n; i++) {
	if ((test_buf[i] == 0xA5) && (test_buf[i + 1] == 0x5A)) {
		if (test_buf[i + 4] == 0x83)
			h = &test_buf[i + 5];
		for (j = i; j < n; j++) {
			if ((test_buf[j] == 0x0D) && (test_buf[j + 1] == 0x0A)) {
				t = &test_buf[j - 1];
				i = n;
				memcpy(x_bak, h, (int) (t - h + 1));
				break;
			}
		}
	}
}

(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(x_bak), x_bak);

//return (*env)->NewStringUTF(env, test_buf);
return bytearray;
}
/*
 * Class:     cedric_serial_SerialPort
 * Method:    repeatRead
 * Signature: ()V
 */
JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_repeatRead(
	JNIEnv *env, jobject thiz) {
int length = 0;
int n_5 = 0;
int n_length = 0;
int ret;
int i = 0;
fd_set rfds;
struct timeval tv;
int sret;
int read_ret = 0;
unsigned char test_buf[25];
unsigned char x_bak[18];
unsigned char header[5];
unsigned char final[29];
unsigned char element[1];
int x_error = 0;
int first = 1;
int second = 0;
int head_not_body = 1;
int n_x = 0;
int module_error = 0;
char timeout[8] = "timeout\0";
unsigned char *h = NULL;
unsigned char *t = NULL;
unsigned char *p_header = NULL;
unsigned char *p_element = NULL;
unsigned char *p_x_bak = NULL;
unsigned char *p_final = NULL;

memset(test_buf, '\0', 25);
memset(x_bak, '\0', 18);
memset(final, '\0', 29);

p_element = element;
p_x_bak = x_bak;
p_final = final;

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");
jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
/* READ */
FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 2;
tv.tv_usec = 0;


while(1){
	FD_ZERO(&rfds);
	FD_SET(descriptor, &rfds);

	sret = select(descriptor + 2, &rfds, NULL, NULL, &tv);
	if(sret < 0){
		LOGD("new select error!");
		return NULL;
	}else if(sret == 0){
		LOGD("<repeat read timeout>");
		memset(final, '\0', 29);
		return (*env)->NewStringUTF(env, timeout);
	}else{
		if(head_not_body){
			/* head */
			LOGD("repeat read start.....................................");
			read(descriptor, element, 1);
			LOGD(">>>>>>>>>>>>>> 0x%x",*element);
			if(first && (*element == 0xA5)){
				LOGD("repeat read 1 = 0x%x", *element);
				first = 0;
				second = 1;
				continue;
			}else if(first){
				LOGD("repeat not read1......");
				x_error++;
				if(x_error == 50){
					LOGD("repeat read return now......");
					return NULL;
				}
				continue;
			}
			if(second && (*element == 0x5A)){
				LOGD("repeat read 2 = 0x%x", *element);
				first = 0;
				second = 0;
				continue;
			}else if(second && (*element == 0xA5)){
				LOGD("repeat in read2 but it's read1");
				first = 0;
				second = 1;
				continue;
			}else if(second){
				LOGD("repeat not read2......");
				x_error++;
				first = 1;
				second = 0;
				continue;
			}
			n_5++;
			if(n_5 == 2){
				if(*element < 1)
					return NULL;
				length = *element - 7;
				LOGD(">>>>>>>>>>>>repeat read body need %d", length);
			}
			if((n_5 == 3) && *element == 0xFF){
				LOGD("WHY?>>>>0x%x",*element);
				module_error = 1;
				continue;
			}
			if((n_5 == 3) && (*element != 0x83)){
				LOGD(">>>>>>>>>>>>repeat 0x83 error!");
				return NULL;
			}
			if((n_5 == 5) && module_error){
				LOGD("modules return 0x%x", *element);
				return NULL;
			}
			if(n_5 == 5){
				LOGD(">>>>>>>>>>>>>repeat body in!");
				head_not_body = 0;
				x_error = 0;
				continue;
			}
		}else{
			/* body */
			read(descriptor, test_buf + n_x, 1);
			n_x++;
			if(n_x == (length)){
				LOGD("repeat okey 18 = %d, process now", n_x);
				break;
			}
		}
	}
}

if ((test_buf[16] == 0x0D) && (test_buf[17] == 0x0A)) {
	memcpy(x_bak, test_buf, 12);
	for (i = 0; i < 12; i++) {

		if ((x_bak[i] >> 4) < 10) { final[2 * i] = ((x_bak[i] >> 4) + 0x30);
		} else {
			final[2 * i] = ((x_bak[i] >> 4) + 0x37);
		}
		if ((x_bak[i] & 0x0F) < 10) {
			final[2 * i + 1] = ((x_bak[i] & 0x0F) + 0x30);
		} else {
			final[2 * i + 1] = ((x_bak[i] & 0x0F) + 0x37);
		}
	}
	final[24] = '\0';
	LOGD("read something %s", final);
}else{
	LOGD(">>>>>>>>>>>>>repeat end error 0x%x 0x%x", test_buf[16], test_buf[17]);
	return NULL;
}
return (*env)->NewStringUTF(env, final);
#if 0
(*(p_header + 2) == 0x8D) {
	length = (*(p_header + 1) - 7);
	while (1) {
		ret = read(descriptor, test_buf + n_length, length - n_length);
		if(ret < 0)
			continue;
		n_length += ret;
		if (n_length == length)
			break;
	}
}
#endif
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    singleRead
 * Signature: ()V
 */
JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_singleRead(
	JNIEnv *env, jobject thiz) {
int retval;
int i = 1;
int j = 0;
int n = 25;
fd_set rfds;
struct timeval tv;
int sret;
int length = 0;
int read_ret = 0;
int x_length = 0;
int x_5 = 0;
int cmd_ret;
char test_buf[25];
char error1[5] = "ERR1\0";
char error2[5] = "ERR2\0";
char error3[5] = "ERR3\0";
char error4[5] = "ERR4\0";
unsigned char x_bak[18];
char R2000single[10] = { 0xA5, 0x5A, 0x00, 0x0A, 0x80, 0x00, 0x64, 0xEE, 0x0D,
		0x0A };
unsigned char test[10];
unsigned char *h = NULL;
unsigned char header[5];
unsigned char *t = NULL;
unsigned char final[29];
unsigned char *p_header = NULL;
unsigned char element[1];

memset(test_buf, '\0', 25);
memset(final, '\0', 29);
memset(x_bak, '\0', 18);
jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");
jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 4;
tv.tv_usec = 0;

tcflush(descriptor, TCIOFLUSH);
write(descriptor, R2000single, 10);

sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
if (sret == -1) {
	LOGD("new select error!");
} else if (sret > 0) {
	/* READ */
	while(1){
	LOGD(">>>>>>>>>>single 1");
		read(descriptor, element, 1);
		if (element[0] == 0xA5) {
			FD_ZERO(&rfds);
			FD_SET(descriptor, &rfds);
			LOGD(">>>>>>>>>>single 2");
			read(descriptor, element, 1);
			if (element[0] == 0x5A) {
				while(1){
					LOGD(">>>>>>>>>>single 3");
					read_ret = read(descriptor, header + x_5, 5 - x_5);
					if(read_ret < 0)
						continue;
					x_5 += read_ret;
					if(x_5 == 5){
						p_header = header;
						break;
					}
				}
				break;
			}
		}else{
			continue;
		}
	}
	if (*(p_header + 2) == 0x81) {
		length = (*(p_header + 1) - 7);
		if(length < 1)
			return NULL;
		while(1){
			FD_ZERO(&rfds);
			FD_SET(descriptor, &rfds);
			LOGD(">>>>>>>>>>single 4");
			read_ret = read(descriptor, test_buf + x_length, length - x_length);
			if(read_ret < 0)
				continue;
			x_length += read_ret;
			if(x_length == length)
				break;
		}

		memcpy(x_bak, test_buf, 12);
		for (i = 0; i < 12; i++) {
			if ((x_bak[i] >> 4) < 10) {
				final[2 * i] = ((x_bak[i] >> 4) + 0x30);
			} else {
				final[2 * i] = ((x_bak[i] >> 4) + 0x37);
			}
			if ((x_bak[i] & 0x0F) < 10) {
				final[2 * i + 1] = ((x_bak[i] & 0x0F) + 0x30);
			} else {
				final[2 * i + 1] = ((x_bak[i] & 0x0F) + 0x37);
			}
		}
		final[28] = '\0';
	} else if (*(p_header + 2) == 0xFF) {
		length = (*(p_header + 1) - 7);
		while(1){
			FD_ZERO(&rfds);
			FD_SET(descriptor, &rfds);
			LOGD(">>>>>>>>>>single 5");
			read_ret = read(descriptor, test_buf + x_length, length - x_length);
			if(read_ret < 0)
				continue;
			x_length += read_ret;
			if(x_length == length)
				break;
		}
		if (*(p_header + 4) == 0x01)
			return (*env)->NewStringUTF(env, error1);
		if (*(p_header + 4) == 0x02)
			return (*env)->NewStringUTF(env, error2);
		if (*(p_header + 4) == 0x03)
			return (*env)->NewStringUTF(env, error3);
		if (*(p_header + 4) == 0x04)
			return (*env)->NewStringUTF(env, error4);
	}
} else {
	LOGD("singleRead timeout");
}

return (*env)->NewStringUTF(env, final);
}
/*
 * Class:     cedric_serial_SerialPort
 * Method:    repeatRead
 * Signature: ()V
 */
JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_scanRead(
	JNIEnv *env, jobject thiz) {
int retval;
fd_set rfds;
struct timeval tv;
int sret;
int i = 0;
int ret = 0;
int length = 0;
int msg_6 = 0;
char test_buf[30];
unsigned char cmdret_buf[6];
unsigned char err_ret[1] = { '\0' };
unsigned char cmdret_cmp[6] = { 0x04, 0xD0, 0x00, 0x00, 0xFF, 0x2C };

memset(test_buf, '\0', 30);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 4;
tv.tv_usec = 0;


sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);
if (sret == -1) {
	LOGD("new select error!");
} else if (sret > 0) {
	ret = read(descriptor, cmdret_buf, 6);
	if(ret != 6)
		return NULL;
	if(0 != (strcmp(cmdret_buf, cmdret_cmp))){
		LOGD("************ hardware");
		LOGD("hardware 6 : %s", cmdret_buf);
		memcpy(test_buf, cmdret_buf, 6);
		retval = read(descriptor, test_buf+6, 24);
		length = strlen(test_buf);
		LOGD("string %d = %s", length, test_buf);
		return (*env)->NewStringUTF(env, test_buf);
	}else{
		retval = read(descriptor, test_buf, 30);
		LOGD("*************** %s, length =%d",test_buf, retval);
		if (retval < 0) {
			LOGD("read error!");
		}
	}
	if((*(test_buf+20) == 0x00) && (*(test_buf+21) == 0x00) && (*(test_buf+22) == 0x00) && (*(test_buf+23) == 0x00)){

	}
} else {
	LOGD("scanRead timeout");
}
LOGD("string = %s", test_buf);
return (*env)->NewStringUTF(env, test_buf);
}
/*
 * Class:     cedric_serial_SerialPort
 * Method:    cash_read
 * Signature: ()V
 */
JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_cashread(
	JNIEnv *env, jobject thiz) {
int retval;
int i = 0;
int j = 0;
int n = 25;
char test_buf[20];
unsigned char status[10];
unsigned char *p = NULL;
unsigned char *b = NULL;
unsigned char final_string[30];

//jbyteArray bytearray;
//bytearray = (*env)->NewByteArray(env,30);

memset(status, '\0', 10);
memset(test_buf, '\0', 20);
memset(final_string, '\0', 30);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

/* READ */
read(descriptor, test_buf, 20);
LOGD("read something %s", test_buf);
#if 1
/* Change the value */
//if(test_buf[0] == 0xAA && test_buf[1] == 0XBB){
strcat(final_string, "|");

/* 锟藉嘲锟斤拷锟姐�锟斤拷*/
p = status;
p = cash_getmoney(test_buf[5]);

strcat(final_string, p);
strcat(final_string, "|");

/* 锟藉嘲锟斤拷锟姐�锟借�锟芥��ワ拷楠����?/
p = status;
p = cash_get_status(test_buf[3]);

strcat(final_string, p);
strcat(final_string, "|");

/* 锟藉嘲锟斤拷锟姐�缁��锟�*/
b = status;
for (j = 0; j < 10; j++) {
	sprintf(b + j, "%c", test_buf[10 + j]);
}

strcat(final_string, b);
strcat(final_string, "|");
#endif

//}
//(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(final_string), final_string);

//return bytearray;

return (*env)->NewStringUTF(env, final_string);
}
#if 0
/*
 * Class:     cedric_serial_SerialPort
 * Method:    newcash_read
 * Signature: ()V
 */
	JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_newcashread
(JNIEnv *env, jobject thiz)
{
	int retval;
	int i = 0;
	int j = 0;
	int n = 25;
	int n_16 = 0;
	int n_x = 0;
	char test_buf[108];
	unsigned char element[1];
	unsigned char header[20];
	unsigned char status[10];
	unsigned char real[52];
	unsigned char *p = NULL;
	unsigned char *b = NULL;
	unsigned char final_string[45];
	unsigned char x_bak[108];
	int length = 0;
	unsigned char *p_header = NULL;

	jbyteArray bytearray;
	bytearray = (*env)->NewByteArray(env,108);

	memset(status, '\0', 10);
	memset(test_buf, '\0', 108);
	memset(final_string, '\0', 45);
	memset(header, '\0', 20);

	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);


	while(1){
		read(descriptor, element, 1);
		if(element[0] == 0xAA){
			read(descriptor, element, 1);
			if(element[0] == 0x55){
				read(descriptor, element, 1);
				if(element[0] == 0xAA){
					read(descriptor, element, 1);
					if(element[0] == 0x55){
						while(1){
							retval = read(descriptor, header + n_16, 16 - n_16);
							n_16 += retval;
							if(n_16 == 16) break;
						}
						break;
					}
				}
			}
		}
	}


	/* READ */
	length = header[0];
	if(length){
		while(1){
			retval= read(descriptor, real + n_x, length - n_x);
			n_x += retval;
			if(n_x == length) break;
		}
	}

	if(header[4] == 0x23){
		/* Change the value */
		//if(test_buf[0] == 0xAA && test_buf[1] == 0XBB){
		strcat(final_string, "|");

		/* ����查��ゆ����锟界�锟斤拷���*/
		p = status;
		p = new_cash_gettype(real[10]);

		strcat(final_string, p);
		strcat(final_string, "|");

		/* ����查��ゆ����锟介�濮�拷���*/
		p = status;
		p = new_cash_getmoney(real[2]);

		strcat(final_string, p);
		strcat(final_string, "|");

		/* ����查��ゆ����锟介��ゆ����*/
		p = status;
		p = new_cash_getyear(real[11]);

		strcat(final_string, p);
		strcat(final_string, "|");

		/* ����查��ゆ����锟界�锟斤拷���*/
		b = status;
		for(j = 0; j < 10; j ++){
			sprintf(b + j, "%c", real[12+j]);
		}

		strcat(final_string, b);
		strcat(final_string, "|");

		/* ����查��ゆ����锟介��ゆ����*/
		p = status;
		p = new_cash_getstatus(real[4]);

		strcat(final_string, p);
		strcat(final_string, "|");
	}
	return (*env)->NewStringUTF(env, final_string);
}
#endif

/*
 * Class:     cedric_serial_SerialPort
 * Method:    isFingerPressed
 * Signature: ()V
 */
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_isFingerPressed(
	JNIEnv *env, jobject thiz) {
int retval = 0;
int n_7 = 0;
int i = 0;
int j = 0;
int n = 25;
const int len = 7;
char test_buf[len];
char temp[70];
char ready_buf[6] = "READY?";
char okey_buf[2] = "OK";

LOGD("isFingerPressed.........");

memset(test_buf, '\0', len);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
LOGD("len = %d", len);

/* READ */
 tcflush(descriptor, TCIFLUSH);

/*
while(1){
	retval = read(descriptor, test_buf + n_7, len - n_7);
	n_7 += retval;
	if (n_7 == len)
		break;
}
*/
/*
LOGD("FingerPressed   do while");
do{
	n_7 = read(descriptor, test_buf, 1);
}while(test_buf[0] != 'R');

LOGD("FingerPressed   while");

while(1){
	n_7 += read(descriptor, test_buf + n_7, len - n_7);
	if (n_7 == len)
		break;
}
*/


n_7 = 0;
while(1){
//	LOGD("xxxxxxxxx");
	n_7 += read(descriptor, test_buf + n_7, 1);
//	LOGD("ffffffffffffxx n_7 = %d", n_7);

	if(n_7 > 0 && test_buf[n_7 - 1] == ready_buf[n_7 - 1]){
		//LOGD("000000000000000000000000000");
			if(n_7 == 6){
					int readLen = 0;
					while(1){
							readLen += read(descriptor, temp, 70 - readLen);
							if(readLen == 70)	break;
				  }
					LOGD("readLen = %d", readLen);
					break;
			}else{
				//LOGD("elseeeeeeeeeeeeeeeee");
					continue;
			}		
	}
	
	n_7 = 0;
}



LOGD("finger read : %x %x %x %x %x %x", test_buf[0], test_buf[1], test_buf[2], test_buf[3], test_buf[4], test_buf[5]);
LOGD("i wanna see: %x %x %x %x %x %x", 'R', 'E', 'A', 'D', 'Y', '?');

if (0 == strncmp(test_buf, ready_buf, 6)) {
	write(descriptor, okey_buf, sizeof(okey_buf));
	LOGD("sending OK...........");
	return 1;
}
return 0;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    fingerRead
 * Signature: ()V
 */
JNIEXPORT jbyteArray JNICALL Java_android_1serialport_1api_SerialPort_fingerRead(
	JNIEnv *env, jobject thiz, jbyte dataType) {
int retval;
int i = 0;
int n = 0;
int lastN = 0;
int all_num = 0;

unsigned char finger_data[66];
unsigned char over_finger_data[7601];
unsigned char tempTop[5];
unsigned char type[7];
unsigned char *p_over_finger_data = NULL;
unsigned char *p_type = NULL;

p_type = type;
p_over_finger_data = over_finger_data;

jbyteArray bytearray;

//memset(over_finger_data, 0x00, 7601);
//memset(finger_data, 0x00, 66);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
LOGD("GetIntField    mFdID = 0x%x",mFdID);
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");
jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

*p_over_finger_data = dataType;
switch(dataType){
	case 1:
		LOGD("type is IVAL");
		n = 0x3;
		lastN = 64;
		break;
	case 2:
		LOGD("type is IMG0");
		n = 0x76;
		lastN = 48;
		break;
}

bytearray = (*env)->NewByteArray(env, 1 + n*64 + lastN);

loop_for_read:

retval = 0;
while (retval != 4) {
	retval += read(descriptor, tempTop + retval, 4 - retval);
}

LOGD("aaaaaaaaa zhangjun info = 0x%x,0x%x,0x%x,0x%x",tempTop[0],tempTop[1],tempTop[2],tempTop[3]);
if(  (tempTop[0]==0x55)  &&  (tempTop[1]==0xaa)  &&  (tempTop[2]==0x55)  &&  (tempTop[3]==0xaa)  )
{
	LOGD("zj the head date is right\n");
}
else
{
	LOGD("zj  the head date  is wrong\n");
	retval = 0;
	while (retval != 1) {
	retval += read(descriptor, tempTop + retval, 1 - retval);
	LOGD("zj  tempTop[0]=0x%x\n",tempTop[0]);
	}
}
retval = 0;
while (retval != 6) {
	retval += read(descriptor, type + retval, 6 - retval);
}

LOGD("zhangjun info = 0x%x,0x%x,0x%x,0x%x,0x%x,0x%x",*(p_type + 0),*(p_type + 1),*(p_type + 2),*(p_type + 3),*(p_type + 4),*(p_type + 5));
   //对数据包进行校验
i = *(p_type + 4);
//i=all_num;

LOGD("i=%d,all_num=%d\n",i,all_num);

if (all_num++ != i) {
	*p_over_finger_data = 4;
	(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
			over_finger_data);
	LOGD("package num error!");
	tcflush(descriptor, TCIFLUSH);
	return bytearray;
}


if (i != n) {
	retval = 0;
	while (retval != 66) {
		retval += read(descriptor, finger_data + retval, 66 - retval);
	}
	memcpy((p_over_finger_data + 1 + i * 64), finger_data, 64);
	LOGD("zhangjun finger_data package : i = %d, length is %d", i, retval);
	goto loop_for_read;
}

retval = 0;
while (retval != 66) {
	retval += read(descriptor, finger_data + retval, 66 - retval);
}

LOGD("package : i = %d, length is %d", i, retval);
memcpy((p_over_finger_data + 1 + i * 64), finger_data, lastN);

(*env)->SetByteArrayRegion(env, bytearray, 0, 1 + n*64 + lastN,
		over_finger_data);
return bytearray;



/*



while (1) {
	n_6 = 0;
	retval = 0;
	read(descriptor, element, 1);
	LOGD("SerialPort_fingerRead   element1 = 0x%x",element[0]);
	if (*element == 0x55) {
		read(descriptor, element, 1);
		LOGD("element2 = 0x%x",element[0]);
		if (*element == 0xAA) {
			read(descriptor, element, 1);
			LOGD("element3 = 0x%x",element[0]);
			if (*element == 0x55) {
				read(descriptor, element, 1);
				LOGD("element4 = 0x%x",element[0]);
				if (*element == 0xAA) {
					while (1) {
						retval = read(descriptor, type + n_6, 6 - n_6);
						n_6 += retval;
						if (n_6 == 6)
							break;
					}
					LOGD("info : i = 0x%x, length is %d", *(p_type + 4), n_6);
					LOGD("info = 0x%x,0x%x,0x%x,0x%x,0x%x,0x%x",*(p_type + 0),*(p_type + 1),*(p_type + 2),*(p_type + 3),*(p_type + 4),*(p_type + 5));
					break;
				}
			}
		}
	}
}


if (all_num++ != *(p_type + 4)) {
	*p_over_finger_data = 4;
	(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
			over_finger_data);
	LOGD("package num error!");
	tcflush(descriptor, TCIOFLUSH);
	return bytearray;
}

if ((*(p_type + 4)) == 0x00) {
	if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D) && (*(p_type + 2) == 0x47)
			&& (*(p_type + 3) == 0x30)) {
		*p_over_finger_data = 2;
		LOGD("type is IMG0");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D)
			&& (*(p_type + 2) == 0x47) && (*(p_type + 3) == 0x31)) {
		*p_over_finger_data = 3;
		LOGD("type is IMG1");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x56)
			&& (*(p_type + 2) == 0x41) && (*(p_type + 3) == 0x4C)) {
		*p_over_finger_data = 1;
		LOGD("type is IVAL");
	} else {
		*p_over_finger_data = 0;
		LOGD("type is MD5");
	}
}

LOGD("type[4] = %d    over_finger_data[0] = %d   *p_over_finger_data = %d", type[4], over_finger_data[0], *p_over_finger_data);

i = *(p_type + 4);
if ((*(p_type + 4)) != 0x76) {
	retval = 0;
	n_66 = 0;
	while (1) {
		retval = read(descriptor, finger_data + n_66, 66 - n_66);
		n_66 += retval;
		if (n_66 == 66)
			break;
	}
	memcpy((p_over_finger_data + 1 + i * 64), finger_data, 64);
	LOGD("package : i = %d, length is %d", i, n_66);
	goto loop_for_read;
}

n_66 = 0;
while (1) {
	retval = read(descriptor, finger_data + n_66, 66 - n_66);
	n_66 += retval;
	if (n_66 == 66)
		break;
}
LOGD("package : i = %d, length is %d", i, n_66);
memcpy((p_over_finger_data + 1 + i * 64), finger_data, 48);

(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
		over_finger_data);
return bytearray;
*/
}


/*
 * Class:     cedric_serial_SerialPort
 * Method:    fingerReadIval
 * Signature: ()V
 */
JNIEXPORT jbyteArray JNICALL Java_android_1serialport_1api_SerialPort_fingerReadIval(
	JNIEnv *env, jobject thiz) {

int retval;
int i = 0;
int n = 25;
int n_66 = 0;
int all_num = 0;
int n_6 = 0;
char test_buf[25];
unsigned char finger_data[66];
unsigned char over_finger_data[257];
unsigned char element[1];
unsigned char type[6];
unsigned char *p_element = NULL;
unsigned char *p_over_finger_data = NULL;
unsigned char *p_type = NULL;

p_element = element;
p_type = type;
p_over_finger_data = over_finger_data;

jbyteArray bytearray;
bytearray = (*env)->NewByteArray(env, 257);

memset(test_buf, '\0', 25);
memset(over_finger_data, 0xFF, 257);
memset(finger_data, 0xFF, 66);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
LOGD("GetIntField    mFdID = 0x%x",mFdID);
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

#if 1
/* READ */
//read(descriptor, test_buf, sizeof(test_buf));
LOGD("fingerReadIval");

loop_for_256:
//i = 0;
//memset(type, 0x00, 6);
//p_type = type;
while (1) {
	n_6 = 0;
	retval = 0;
	read(descriptor, element, 1);
	LOGD("fingerReadIval   element1 = 0x%x",element[0]);
	if (*element == 0x55) {
		read(descriptor, element, 1);
		LOGD("element2 = 0x%x",element[0]);
		if (*element == 0xAA) {
			read(descriptor, element, 1);
			LOGD("element3 = 0x%x",element[0]);
			if (*element == 0x55) {
				read(descriptor, element, 1);
				LOGD("element4 = 0x%x",element[0]);
				if (*element == 0xAA) {
					while (1) {
						retval = read(descriptor, type + n_6, 6 - n_6);
						n_6 += retval;
						if (n_6 == 6)
							break;
					}
					LOGD("info : i = 0x%x, length is %d", *(p_type + 4), n_6);
					LOGD("info = 0x%x,0x%x,0x%x,0x%x,0x%x,0x%x",*(p_type + 0),*(p_type + 1),*(p_type + 2),*(p_type + 3),*(p_type + 4),*(p_type + 5));
					break;
				}
			}
		}
	}else{
		break;
	}
}
if (all_num++ != *(p_type + 4)) {
	*p_over_finger_data = 4;
	(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
			over_finger_data);
	LOGD("package num error!");
	tcflush(descriptor, TCIOFLUSH);
	return bytearray;
}

if ((*(p_type + 4)) == 0x00) {
	if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D) && (*(p_type + 2) == 0x47)
			&& (*(p_type + 3) == 0x30)) {
		*p_over_finger_data = 2;
		LOGD("type is IMG0");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D)
			&& (*(p_type + 2) == 0x47) && (*(p_type + 3) == 0x31)) {
		*p_over_finger_data = 3;
		LOGD("type is IMG1");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x56)
			&& (*(p_type + 2) == 0x41) && (*(p_type + 3) == 0x4C)) {
		*p_over_finger_data = 1;
		LOGD("type is IVAL");
	} else {
		*p_over_finger_data = 0;
		LOGD("type is MD5");
	}
}

LOGD("type[4] = %d    over_finger_data[0] = %d   *p_over_finger_data = %d", type[4], over_finger_data[0], *p_over_finger_data);

i = *(p_type + 4);
if ((*(p_type + 4)) != 0x3) {
	retval = 0;
	n_66 = 0;
	LOGD("type[4] = %d    over_finger_data[0] = %d   *p_over_finger_data = %d", type[4], over_finger_data[0], *p_over_finger_data);
	LOGD("p_over_finger_data = %d   finger_data = %d", p_over_finger_data, finger_data);
	while (1) {
		retval = read(descriptor, finger_data + n_66, 66 - n_66);
		n_66 += retval;
		if (n_66 == 66)
			break;
	}
	LOGD("type[4] = %d    over_finger_data[0] = %d   *p_over_finger_data = %d", type[4], over_finger_data[0], *p_over_finger_data);
	memcpy((p_over_finger_data + 1 + i * 64), finger_data, 64);
	LOGD("package : i = %d, length is %d", i, n_66);
	goto loop_for_256;
}

n_66 = 0;
while (1) {
	retval = read(descriptor, finger_data + n_66, 66 - n_66);
	n_66 += retval;
	if (n_66 == 66)
		break;
}
LOGD("package : i = %d, length is %d", i, n_66);
memcpy((p_over_finger_data + 1 + i * 64), finger_data, 64);
#endif
(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
		over_finger_data);
return bytearray;

		
/*		
int retval;
int i = 0;
int n = 25;
int n_66 = 0;
int n_6 = 0;
unsigned char finger_data[64];
unsigned char over_finger_data[257];
unsigned char element[1];
unsigned char type[6];
unsigned char *p_element = NULL;
unsigned char *p_over_finger_data = NULL;
unsigned char *p_type = NULL;

p_element = element;
p_type = type;
p_over_finger_data = over_finger_data;

jbyteArray bytearray;
bytearray = (*env)->NewByteArray(env, 257);

memset(over_finger_data, 0xFF, 257);
memset(finger_data, 0xFF, 64);

jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");

jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

loop_for_256:
LOGD("4th!!!!!!!!!!!!!!!!!!!!!!!!!");
while (1) {
	n_6 = 0;
	retval = 0;
	read(descriptor, element, 1);
	if (*element == 0x55) {
		read(descriptor, element, 1);
		if (*element == 0xAA) {
			read(descriptor, element, 1);
			if (*element == 0x55) {
				read(descriptor, element, 1);
				if (*element == 0xAA) {
					while (1) {
						retval = read(descriptor, type + n_6, 6 - n_6);
						n_6 += retval;
						if (n_6 == 6)
							break;
					}
					break;
				}
			}
		}
	}
}

if ((*(p_type + 4)) == 0x00) {
	if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D) && (*(p_type + 2) == 0x47)
			&& (*(p_type + 3) == 0x30)) {
		*p_over_finger_data = 2;
		LOGD("type is IMG0");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x4D)
			&& (*(p_type + 2) == 0x47) && (*(p_type + 3) == 0x31)) {
		*p_over_finger_data = 3;
		LOGD("type is IMG1");
	} else if ((*p_type == 0x49) && (*(p_type + 1) == 0x56)
			&& (*(p_type + 2) == 0x41) && (*(p_type + 3) == 0x4C)) {
		*p_over_finger_data = 1;
		LOGD("This func is test the ival!!");
		LOGD("type is IVAL");
	} else {
		*p_over_finger_data = 0;
		LOGD("type is MD5");
	}
}

i = *(p_type + 4);
LOGD("ival ==> %d", i);
if ((*(p_type + 4)) != 0x3) {
	retval = 0;
	n_66 = 0;
	while (1) {
		retval = read(descriptor, finger_data + n_66, 65 - n_66);
		n_66 += retval;
		if (n_66 == 65)
			break;
	}
	memcpy((p_over_finger_data + 1 + i * 64), finger_data, 64);
	goto loop_for_256;
}
while (1) {
	retval = read(descriptor, finger_data + n_66, 65 - n_66);
	n_66 += retval;
	if (n_66 == 65)
		break;
}

memcpy((p_over_finger_data + 1 + i * 64), finger_data, 48);

(*env)->SetByteArrayRegion(env, bytearray, 0, sizeof(over_finger_data),
		over_finger_data);
return bytearray;
*/
}



JNIEXPORT jshort JNICALL Java_android_1serialport_1api_SerialPort_CRC16(
	JNIEnv *env, jobject thiz, jbyteArray ptr, jint count) {
	unsigned short tmpq, tmps;
	unsigned char i = 0;
	unsigned char *s;
	jbyte *bytes = (*env)->GetByteArrayElements(env, ptr, 0);
	s = (unsigned char *)bytes;

	tmps = tmpq = 0;
	while (count-- > 0)	{
		tmpq += s[i++] ;
		tmps += tmpq % 0x10000;
	}
	(*env)->ReleaseByteArrayElements(env, ptr, bytes, 0);
	(*env)->DeleteLocalRef(env, ptr);
	(*env)->DeleteLocalRef(env, s);
	return (tmps & 0xffff);
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    repeatRead
 * Signature: ()V
 */
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_gettemp(
	JNIEnv *env, jobject thiz) {
int length = 0;
int n_3 = 0;
int n_length = 0;
int ret;
int i = 0;
fd_set rfds;
struct timeval tv;
int sret;
int read_ret = 0;
unsigned char element[1];
int x_error = 0;
int first = 1;
int second = 0;
int head_not_body = 1;
int n_x = 0;
int module_error = 0;
unsigned char get_t[8] = {0xA5,0x5A,0x00,0x08,0x34,0x3C,0x0D,0x0A};
char get[6];
int temp = 0;
unsigned int temp_1;

memset(get, '\0', 6);
jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");
jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
/* READ */
tcflush(descriptor, TCIOFLUSH);

FD_ZERO(&rfds);
FD_SET(descriptor, &rfds);

tv.tv_sec = 2;
tv.tv_usec = 0;


ret = write(descriptor, get_t, 8);
LOGD("write_get_t okey");
if(ret != 8){
	LOGD("GET WRITE ... ERROR!");
	return -1;
}
while(1){
	FD_ZERO(&rfds);
	FD_SET(descriptor, &rfds);

	sret = select(descriptor + 2, &rfds, NULL, NULL, &tv);
	if(sret < 0){
		LOGD("new select error!");
		return -1;
	}else if(sret == 0){
		LOGD("<get_t read timeout>");
		return -1;
	}else{
		if(head_not_body){
			/* head */
			read(descriptor, element, 1);
			LOGD("temp data >>>>>> 0x%x", *element);
			if(first && (*element == 0xA5)){
				LOGD("temp 1");
				first = 0;
				second = 1;
				continue;
			}else if(first){
				x_error++;
				if(x_error == 50){
					return -1;
				}
				continue;
			}
			if(second && (*element == 0x5A)){
				LOGD("temp 2");
				first = 0;
				second = 0;
				continue;
			}else if(second && (*element == 0xA5)){
				first = 0;
				second = 1;
				continue;
			}else if(second){
				x_error++;
				first = 1;
				second = 0;
				continue;
			}
			n_3++;
			if(n_3 == 2){
				if(*element < 1)
					return -1;
				length = *element - 5;
				LOGD(">>>>>>>>>>>>get_t read body need %d", length);
			}
			if((n_3 == 3) && *element == 0xFF){
				module_error = 1;
				continue;
			}
			if((n_3 == 3) && (*element != 0x35)){
				LOGD("WHY?>>>>>>>0x%x", *element);
				first = 1;
				second = 0;
				continue;
			}
			if((n_3 == 3) && (*element == 0x35)){
				LOGD(">>>>>>>>>>>>>get_t body in!");
				head_not_body = 0;
				x_error = 0;
				continue;
			}
		}else{
			/* body */
			read(descriptor, get + n_x, 1);
			n_x++;
			if(n_x == (length)){
				LOGD("get_t okey 6 = %d, process now", n_x);
				break;
			}
		}
	}
}
if(get[0] == 0x01){
	LOGD("get1 0x%x.....get2 0x%x", get[1], get[2]);
	if((get[1] >> 7) == 1){
		LOGD("1>>>%x",get[1] <<8);
		LOGD("2>>>%x",get[2]);
		temp_1 = ~((get[1] << 8) + get[2]) + 1;
		LOGD("temp_1 == %x", temp_1);
		temp = temp_1 /100;
	}else{
		LOGD("1>>>%x",get[1] <<8);
		LOGD("2>>>%x",get[2]);
		temp_1 = ((get[1] << 8) + get[2]);
		LOGD("temp_1 == %x", temp_1);
		temp = temp_1 / 100;
	}
	LOGD("temp = %d", temp);
}
return temp;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    wireless_write
 * Signature: ()V
 */

JNIEXPORT jstring JNICALL Java_android_1serialport_1api_SerialPort_fingerVerify2(
	JNIEnv *env, jobject thiz, jbyteArray bytearray) {
int retval = 0;
char read_buf[257];
char *p_read_buf = read_buf;
char *send_array;
int length = 257;
char *s;
int i = 0;
char verify = 0;

LOGD("fingerVerify zj start>>>>>>>>>>");
memset(read_buf, '\0', 257);
LOGD("15151515151515");
jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
LOGD("141414141414141414141414");
jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
LOGD("131313131313131313131313");
jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
		"Ljava/io/FileDescriptor;");
LOGD("121212121zj 2121212122");
jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
		"descriptor", "I");
LOGD("100000000000000000000000");
jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
LOGD("9999999999999999999");
jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
LOGD("88888888888888888");
jbyte *bytes = (*env)->GetByteArrayElements(env, bytearray, 0);
LOGD("xxxxxxxxxxxxxxxzj xxxxxxxx bytes=%s\n",bytes);
LOGD("xxxxxxxxxxxxxxxzj xxxxxxxx bytes=%c\n",bytes[0]);
LOGD("1616161616161616161616");
s = (char *)bytes;
LOGD("777777zj77777777777777");
verify = LRC(s, 256);
LOGD("write >>>>> verify LRC = %x", verify);

//char test1[]="1234";
//char test2[]="4321";

//write(descriptor, test1, 4);
LOGD("xxxxxxxxxxxxxxxzj xxxxxxxx s=%s\n",s);
retval = write(descriptor, s, 256);

LOGD("Send finger_verify retval = %d   ", retval);
retval += write(descriptor, &verify, 1);
LOGD("Send finger_verify retval = %d   ", retval);
//write(descriptor, test2, 4);

/*
if(retval != length){
	(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
	(*env)->DeleteLocalRef(env, bytearray);
	(*env)->DeleteLocalRef(env, bytes);
	(*env)->DeleteLocalRef(env,SerialPortClass);
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	(*env)->DeleteLocalRef(env,mFdID);
	(*env)->DeleteLocalRef(env,descriptorID);
	(*env)->DeleteLocalRef(env,mFd);

	LOGD("finger_verify retval = %d", retval);
	return NULL;
}*/

/*
retval = 0;
while (retval != 257){
	retval += read(descriptor, read_buf + retval, 257 - retval);
}
LOGD("finger_verify length = %d", retval);
//verify = LRC(p_read_buf, 256);


LOGD("finger verify ret >>>>>>> %s\n", read_buf);
*/
//LOGD("finger lrc = %x\n", verify);
//LOGD("return lrc = %x\n", *(p_read_buf + 256));

(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
LOGD("11111111111111111111111");
(*env)->DeleteLocalRef(env, bytearray);
LOGD("222222222222222222222222");
//(*env)->DeleteLocalRef(env, bytes);
(*env)->DeleteLocalRef(env,SerialPortClass);
LOGD("3333333333333333333333");
(*env)->DeleteLocalRef(env,FileDescriptorClass);
LOGD("444444444444444444444");
//(*env)->DeleteLocalRef(env,mFdID);
//(*env)->DeleteLocalRef(env,descriptorID);
(*env)->DeleteLocalRef(env,mFd);
LOGD("5555555555555555555555");
return (*env)->NewStringUTF(env, read_buf);
}

 /*
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_fingerVerify(
	JNIEnv *env, jobject thiz, jbyteArray SrcTemplet, jbyteArray DstTemplet) {

unsigned char *s;
unsigned char *d;
jbyte *stemp = (*env)->GetByteArrayElements(env, SrcTemplet, 0);
jbyte *dtemp = (*env)->GetByteArrayElements(env, DstTemplet, 0);

s = (unsigned char *)stemp;
d = (unsigned char *)dtemp;

// jint result = Match2Fp(s,d);
	jint result = add(5,3);

(*env)->DeleteLocalRef(env, SrcTemplet);
(*env)->DeleteLocalRef(env, DstTemplet);

return result;
}
*/
/*
 * Class:     cedric_serial_SerialPort
 * Method:    readBag
 * Signature: ()V
 */

int readBag(char bag[], jint descriptor, jint delay, int len)
{
	fd_set rfds;
	struct timeval tv;
	int sret;
	int i = 0;
	FD_ZERO(&rfds);
	FD_SET(descriptor, &rfds);
	tv.tv_sec = delay;
	tv.tv_usec = 0;

	tcflush(descriptor, TCIFLUSH);
	LOGD(">>>>>>>>>>>>>start read");
	while(i < len){
		FD_ZERO(&rfds);
		FD_SET(descriptor, &rfds);
		sret = select(descriptor + 1, &rfds, NULL, NULL, &tv);

		if(sret < 0){
			return 0;
		}else if(sret == 0){
			LOGD("<read timeout>");
			return 0;
		}else{
			read(descriptor, bag + i, 1);
			LOGD(">>>>>>>>>>>>>>all read %d: %d", i, bag[i]);
			++i;
		}
	}
	return 1;
}

JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_serialRead(JNIEnv *env, jobject thiz,
		jbyteArray bytearray, jint len) {
	LOGD("CmdRead  000000000000");
	jbyte *bytes = (*env)->GetByteArrayElements(env, bytearray, 0);
	jint length = (*env)->GetArrayLength(env, bytearray);
	jint result;
	char *s = (char*)bytes;
	memset(s, 0, length);

	 jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	 jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	 jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
	 		"Ljava/io/FileDescriptor;");
	 jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
	 		"descriptor", "I");

	 jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	 jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	result = readBag(s, descriptor, 2, len); //2s锟斤拷时
	LOGD("CmdRead  1");
	(*env)->DeleteLocalRef(env,SerialPortClass);
	LOGD("CmdRead  2");
	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	LOGD("CmdRead  3");
//	(*env)->DeleteLocalRef(env,mFdID);
	LOGD("CmdRead  4");
//	(*env)->DeleteLocalRef(env,descriptorID);
	LOGD("CmdRead  5");
	(*env)->DeleteLocalRef(env,mFd);
	LOGD("CmdRead  5.1");
//	(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
//	(*env)->DeleteLocalRef(env, bytes);
	LOGD("CmdRead  5.2");
//	(*env)->DeleteLocalRef(env, s);
	LOGD("CmdRead  6");
	return result;  //1姝ｇ‘锛?0 閿欒

}
//GPRS写操作
JNIEXPORT jint JNICALL Java_android_1serialport_1api_SerialPort_serialWrite(
	 JNIEnv *env, jobject thiz, jbyteArray bytearray) {
	 int retval = 0;
	 int length = 0;
	 int allret = 0;
	 char *s;
	 int i = 0;
	 LOGD("wireless Cmd");
	 jbyte *bytes = (*env)->GetByteArrayElements(env, bytearray, 0);

	 jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	 jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	 jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd",
	 		"Ljava/io/FileDescriptor;");
	 jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass,
	 		"descriptor", "I");

	 jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	 jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

//	 write(descriptor, "12345678901234567890123456789012345678901234567890123456789012345678901234567890\0", 80);
//	 tcflush(descriptor, TCIFLUSH);
	 s = (char *)bytes;
	 length = (*env)->GetArrayLength(env, bytearray);
	 for(i = 0; i < length; i++){
	 	 LOGD("write : 0x%x", *(s+i));
	 }
	 while(retval < length){
		 retval += write(descriptor, s+retval, length-retval);
	 }
//	 retval = write(descriptor, s, length);
	 LOGD("wirelessCmdWrite retval = %d", retval);
//	 fflush(descriptor);
	 if(retval != length){
	 	(*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
	 	(*env)->DeleteLocalRef(env, bytearray);
	 	LOGD("wireless write length = %d", length);
	 //	(*env)->DeleteLocalRef(env, bytes);
	 	(*env)->DeleteLocalRef(env,SerialPortClass);
	 	(*env)->DeleteLocalRef(env,FileDescriptorClass);
	// 	(*env)->DeleteLocalRef(env,mFdID);
	// 	(*env)->DeleteLocalRef(env,descriptorID);
	 	(*env)->DeleteLocalRef(env,mFd);
	 	return 0;
	 }
	 (*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);
	 (*env)->DeleteLocalRef(env, bytearray);
	 LOGD("wireless write length = %d", length);
	// (*env)->DeleteLocalRef(env, bytes);
	 LOGD("wirelessCmdWrite   1");
	 (*env)->DeleteLocalRef(env,SerialPortClass);
	 LOGD("wirelessCmdWrite   2");
	 (*env)->DeleteLocalRef(env,FileDescriptorClass);
	 LOGD("wirelessCmdWrite   3");
//	 (*env)->DeleteLocalRef(env,mFdID);
	 LOGD("wirelessCmdWrite   4");
//	 (*env)->DeleteLocalRef(env,descriptorID);
	 LOGD("wirelessCmdWrite   5");
	 (*env)->DeleteLocalRef(env,mFd);
	 LOGD("wirelessCmdWrite   6");
	 return 1;
}
