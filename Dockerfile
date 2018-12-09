FROM java:8
VOLUME /tmp
ARG DEPENDENCY=target/dependency
# Installed with homebrew with a symlink
CMD ["gradle", "/usr/local/bin/gradle"]
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.iheartmedia.IHeartMedia"]
