#!/usr/bin/env bash

MIXEDLIST_FILE=../data/mixedlist.json
SCRIPTS_DIR=$(realpath $(dirname $0))
cd ${SCRIPTS_DIR}

echo [INFO] Downloading RuneWatch mixedlist
status_code=`curl \
      --max-time 5 \
      -w "%{http_code}" \
      -H "Accept: application/json" \
      https://runewatch.com/api/cases/mixedlist \
      --output ${MIXEDLIST_FILE} 2> /dev/null`

# make code 500 exit 0 to reduce email spam when runewatch is down 
if [[ ${status_code} == 500 || ${status_code} == 502 ]]; then
  echo "[ERROR] RuneWatch returned 500 status code"
  exit 0
elif [[ ${status_code} != 200 ]]; then
  echo "[ERROR] RuneWatch returned non-200 status code: ${status_code}"
  exit 1
fi

# make sure file exists AND sanity check on the file size
if [[ $(find ${MIXEDLIST_FILE} -type f -size -100c 2>/dev/null) ]]; then
  echo "[ERROR] mixedlist filesize less than 100 bytes"
  exit 1
fi

if [[ -z $(git status -s) ]]; then
  echo "[INFO] No changes detected"
  exit 0
fi


cur_datetime=`date -u +%FT%TZ`

git config --local user.name RW-Updater
git config --local user.email "rw-updater@runewatch.com"
git add ${MIXEDLIST_FILE}
git commit -m "[${cur_datetime}] watchlist update"
git push origin runewatch

echo "[INFO] Watchlist successfully updated"
