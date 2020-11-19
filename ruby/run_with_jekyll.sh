#!/usr/bin/env bash

if ! docker inspect ruby_with_jekyll > /dev/null 2>&1; then
  docker build -t ruby_with_jekyll ./docker_images/jekyll
fi

docker run -it --rm --name jekyll \
    --volume=$PWD:/srv/jekyll \
    -p 1234:1234 \
     ruby_with_jekyll \
     $1
