#!/usr/bin/env bash

if ! docker inspect ruby_with_liquid > /dev/null 2>&1; then
  docker build -t ruby_with_liquid ./docker_images/liquid
fi

docker run -it --rm --name liquid \
    --volume=$PWD:/srv/liquid \
     ruby_with_liquid \
     $1
