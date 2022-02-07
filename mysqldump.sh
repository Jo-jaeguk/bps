#!/bin/sh
 

# 백업 위치를 /backup 아래로 정한다.

# 백업 시간을 년-월-일 형식으로 지정한다.
DATE=`date +"%Y%m%d%H%M%S"`

 

# 사용자 계정과 비밀번호

USERNAME="bpsuser"
PASSWORD="P@ssw0rd"

BACKUP_PATH=/home/bpsuser/dbbackup

# 백업할 데이타베이스
DATABASE="bps"

BEFORE=`date -d yesterday +"%Y%m%d"`
echo ${BEFORE}
rm -rf $BACKUP_PATH/mysql_db_bak_${BEFORE}*.sql

# 백업 작업
mysqldump -u$USERNAME -p$PASSWORD  $DATABASE > $BACKUP_PATH/mysql_db_bak_${DATE}.sql



