module m1(input in1,in2,in3 output out1);     
	wire w1;
	assign w1 = in1 + in2;  
	assign out1 = w1 + in3;	
endmodule
