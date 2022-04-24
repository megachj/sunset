FROM nginx:1.21.6
LABEL MAINTAINER="sunset.choe"

COPY conf/nginx.conf /etc/nginx/conf.d/nginx.conf

CMD ["nginx", "-g", "daemon off;"]

EXPOSE 80
