#!/bin/bash
BASE_PATH=/home/bpsuser
SERVER_ONE_PATH=$BASE_PATH/admin

SERVER_ONE_JAR=admin-one-spring.jar

ORIGIN_JAR_NAME=app-admin-0.0.1-SNAPSHOT.jar

ASIDE_TARGET_PROFILE="prod"

TARGET_PORT=8090
TARGET_PROFILE=$ASIDE_TARGET_PROFILE


BUILD_PATH=$(ls $BASE_PATH/$ORIGIN_JAR_NAME)
JAR_NAME=$(basename $BUILD_PATH)

echo "> build 파일명: $JAR_NAME"


TARGET_ONE="none"
TARGET_TWO="none"

RES_CODE=$(curl -o /dev/null -w "%{http_code}" "http://localhost:8090/login")
if [ $RES_CODE -eq 200 ];
then
  echo "OKOK"
  TARGET_ONE="live"
fi

echo $TARGET


if [ $TARGET_ONE ==  "live" ];
then
 echo "> 살았음 "
 FIRST_PATH=$SERVER_ONE_PATH
 FIRST_JAR=$SERVER_ONE_JAR

 IDLE_PID=$(pgrep -f $FIRST_JAR)

 if [ -z $IDLE_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 $IDLE_PID
  sleep 5
fi

else
 echo "there is no alive server"
	FIRST_PATH=$SERVER_ONE_PATH
	FIRST_JAR=$SERVER_ONE_JAR
fi

echo "> 첫번째 경로 -> $FIRST_PATH  JAR 명 -> $FIRST_JAR"


cp -rf $ORIGIN_JAR_NAME $FIRST_PATH/
mv -f $FIRST_PATH/$JAR_NAME  $FIRST_PATH/$FIRST_JAR
rm -rf $FIRST_PATH/$JAR_NAME

echo "> 첫번째 경로  배포!!  -> $FIRST_PATH  JAR 명 -> $FIRST_JAR"
nohup java -jar -Dspring.profiles.active=$TARGET_PROFILE $FIRST_PATH/$FIRST_JAR &

echo "> 배포 완료 확인 체크 시작"


sleep 10
for retry_count in {1..10}
do
	RES_CODE=$(curl -o /dev/null -w "%{http_code}" "http://localhost:$TARGET_PORT/login")
	if [ $RES_CODE -eq 200 ];
	then
	  echo "> $FIRST_JAR  배포 완료!!"
	  break
	fi

	if [ $retry_count -eq 10 ];
	then
		echo "> Health check 실패. "
		echo "> Nginx에 연결하지 않고 배포를 종료합니다."
		exit 1
	fi

	echo "> Health check 연결 실패. 재시도..."
	sleep 10

done
