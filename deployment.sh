cd ../fabric-samples/test-network
./network.sh down
./network.sh up
./network.sh createChannel

./network.sh deployCC -ccn UPMPoly -ccp ../../UPMPoly -ccl java
