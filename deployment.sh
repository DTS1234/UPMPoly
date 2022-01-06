cd /fabric-samples/test-network
./network.sh down
./network.sh up
./network.sh createChannel

peer lifecycle chaincode package UPMPoly.tar.gz --path ../../UPMPoly/build/install/UPMPoly --lang java --label UPMPoly_1.0

peer lifecycle chaincode install UPMPoly.tar.gz

peer lifecycle chaincode queryinstalled

peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile /home/vagrant/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --channelID mychannel --name UPMPoly --version 1.0 --package-id UPMPoly_1.0:a52ebe201af4aa81d68e88bbd44dea3d4d67210611491a2dd36770318e8db71f --sequence 1

peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name UPMPoly --version 1.0 --sequence 1 --output json
peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name UPMPoly --version 1.0 --sequence 1 --output json

peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile /home/vagrant/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --channelID mychannel --name UPMPoly --version 1.0 --package-id UPMPoly_1.0:a52ebe201af4aa81d68e88bbd44dea3d4d67210611491a2dd36770318e8db71f --sequence 1

peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name UPMPoly --version 1.0 --sequence 1 --output json
peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name UPMPoly --version 1.0 --sequence 1 --output json

peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile /home/vagrant/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --channelID mychannel --name UPMPoly --peerAddresses localhost:7051 --tlsRootCertFiles /home/vagrant/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles /home/vagrant/fabric-samples/test-network/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt --version 1.0 --sequence 1

peer lifecycle chaincode querycommitted --channelID mychannel --name UPMPoly
peer lifecycle chaincode querycommitted --channelID mychannel --name UPMPoly

export FABRIC_CFG_PATH=$PWD/../config/

export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051