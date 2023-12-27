FROM ruby:3.3-alpine

RUN mkdir -p /usr/local/etc \
  && { \
    echo 'install: --no-document'; \
    echo 'update: --no-document'; \
  } >> /etc/gemrc

RUN apk update && apk add --no-cache \
  build-base \
  libc6-compat

RUN gem install jekyll

RUN mkdir -p /srv/jekyll
WORKDIR /srv/jekyll
VOLUME  /srv/jekyll

EXPOSE 1234

# ENTRYPOINT ["sh"]
ENTRYPOINT ["ruby"]
