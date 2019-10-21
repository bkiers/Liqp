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
  libssl1.1 \
  libc6-compat

RUN gem install jekyll

RUN mkdir -p /srv/jekyll
WORKDIR /srv/jekyll
VOLUME  /srv/jekyll

ENTRYPOINT ["ruby"]
