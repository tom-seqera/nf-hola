clean:
	rm -rf .nextflow*
	rm -rf work
	rm -rf build
	./gradlew clean

assemble:
	./gradlew assemble

