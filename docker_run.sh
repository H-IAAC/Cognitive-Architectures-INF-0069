#!/bin/bash
PKG_OK=$(dpkg-query -W --showformat='${Status}\n' xauth|grep "install ok installed")
if [ "" = "$PKG_OK" ]; then
    echo "Package xauth not installed. Setting up xauth"
    sudo apt install xauth -y
fi

xhost +

XAUTH=`xauth info | grep file | awk '{print $3}'`

sudo docker run --rm -it --name arch_examples \
    -e DISPLAY \
    --net=host \
    --device /dev/snd \
    --privileged \
    -v $XAUTH:/root/.Xauthority \
    -e PULSE_SERVER=unix:${XDG_RUNTIME_DIR}/pulse/native \
    -v ${XDG_RUNTIME_DIR}/pulse/native:${XDG_RUNTIME_DIR}/pulse/native \
    -v ~/.config/pulse/cookie:/root/.config/pulse/cookie \
    --group-add $(getent group audio | cut -d: -f3) \
    brgsil/cog_arq_examples

xhost -
