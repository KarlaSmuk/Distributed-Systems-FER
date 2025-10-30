package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.model.Reading;
import io.grpc.stub.StreamObserver;

public class SensorService extends SensorGrpc.SensorImplBase {

    @Override
    public void requestSensor(SensorDataRequest request, StreamObserver<SensorDataResponse> responseObserver) {

        SensorDataResponse response = SensorDataResponse.newBuilder()
                .setTemperature(reading.getTemperature())
                .setPressure(reading.getPressure())
                .setHumidity(reading.getHumidity())
                .setCO(reading.getCo())
                .setNO2(reading.getNo2())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
