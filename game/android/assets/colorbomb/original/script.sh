#!/bin/bash
# references: 
# https://stackoverflow.com/questions/125281/
# https://unix.stackexchange.com/questions/86722/
# https://stackoverflow.com/questions/20796200/
# https://stackoverflow.com/questions/10515964/
mkdir -p ../resized
# mkdir -p ../combined
for directory in */ ; do
    i=0
    j=0
    for filename in $directory*.png; do
        # echo ${directory%%/}
        # echo ${filename##*/}
        # convert $filename -trim -resize 480x480 ../resized/${directory%%/}_$i.png
	if ((i > 10)); then
		cp $filename ../resized/${directory%%/}_$j.png
		j=$((j+1))
	fi
	i=$((i+1))
	if ((i == 50)); then
		break
	fi
    done
    # convert -append ../resized/${directory%%/}*.png ../combined/${directory%%/}.png
done

