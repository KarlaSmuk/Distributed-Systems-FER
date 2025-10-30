package hr.fer.tel.rassus.client;

import io.grpc.stub.StreamObserver;

public class SensorService extends SensorGrpc.SensorImplBase {

    @Override
    public void requestSensor(SensorDataRequest request, StreamObserver<SensorDataResponse> responseObserver) {

        SensorDataResponse response = SensorDataResponse.newBuilder().build();
//                .setTemperature()
//                .setPressure()
//                .setHumidity()
//                .setCO()
//                .setNO2()
//                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
