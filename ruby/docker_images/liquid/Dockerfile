FROM alpine:latest

RUN mkdir -p /usr/local/etc \
  && { \
    echo 'install: --no-document'; \
    echo 'update: --no-document'; \
  } >> /etc/gemrc

RUN apk update && apk add --no-cache \
  ruby \
  ruby-irb \
  ruby-json \
  ruby-bundler \
  ruby-bigdecimal \
  ruby-dev \
  build-base \
  libc6-compat \
  linux-headers

RUN gem install liquid
RUN gem install ruby-debug-ide

RUN mkdir -p /srv/liquid
WORKDIR /srv/liquid
VOLUME  /srv/liquid

ENTRYPOINT ["ruby"]
