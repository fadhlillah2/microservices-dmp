APP_IMAGE_LIST ?= auth-service

start : down remove up

down :
	docker compose down

remove :
	docker image rm -f ${APP_IMAGE_LIST}

up:
	docker compose up -d

build:
	docker compose build

ls:
	docker compose ps

restart: down up

env:
	cp .env.example .env
	nano .env