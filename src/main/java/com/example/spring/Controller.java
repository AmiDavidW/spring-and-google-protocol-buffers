package com.example.spring;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.OrderService.OrderResponse.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

@RestController
@RequestMapping("/order")
public class Controller {

	private OrderService.OrderResponse response;

	@Autowired
	public Controller(@Value("${order.item.number}") String orderItemNumArg) {
		int orderItemNum = 1;

		try {
			orderItemNum = Integer.parseInt(orderItemNumArg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assert (orderItemNum > 0);
		this.response = this.buildResponse(orderItemNum);
	}

	@PostMapping()
	public @ResponseBody OrderService.OrderResponse create(@RequestBody OrderService.OrderRequest payload) {

		if (payload == null) {
			return OrderService.OrderResponse.newBuilder().build();
		}

		System.out.println(payload);

		return this.response;
	}

	private OrderService.OrderResponse buildResponse(int orderItemNum) {

		Builder orderBuilder = createOrderBuilder();

		// The orderBuilder has one order item already.
		int extraOrderItemNum = orderItemNum - 1;

		if (extraOrderItemNum > 0) {
			com.example.spring.OrderService.OrderItems.Builder itemBuilder = OrderService.OrderItems.newBuilder();
			try (Reader resourceAsStream = new BufferedReader(new InputStreamReader(
				this.getClass().getClassLoader().getResourceAsStream("template-data/response-order-item.json")));) {
				JsonFormat.parser().ignoringUnknownFields().merge(resourceAsStream, itemBuilder);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < extraOrderItemNum; i++) {
				orderBuilder.addOrderItems(itemBuilder.build());
			}
		}

		return orderBuilder.build();
	}

	private Builder createOrderBuilder() {
		Builder orderBuilder = OrderService.OrderResponse.newBuilder();
		try (Reader resourceAsStream = new BufferedReader(new InputStreamReader(
			this.getClass().getClassLoader().getResourceAsStream("template-data/response.json")));) {
			JsonFormat.parser().ignoringUnknownFields().merge(resourceAsStream, orderBuilder);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return orderBuilder;
	}
}
