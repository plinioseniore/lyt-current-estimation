// Inputs are frequency (f), delay (d) and descending time (D) in milliseconds
// Outputs are current (i) and time (t) as array
function [i, t] = spsm_current(f, d, D)

	// Total time as period
	P = 1/f;

	// Max value
	M = 1;

	// Step as fraction of total time
	samples = 10000;
	s = P/samples;

	// Time array in milliseconds
	t=1000*(0:s:P);
	
	// Fill the output of zeros for the delay time
	if (d > P*1000) then d = P*1000; end
	j=1;
	while (t(j) < d)
		i(j) = 0;
		j=j+1;
	end

	// Create a tooth square
	if (j < samples) then
		
		// Start the wave
		i(j) = 1;
		j=j+1;

		// After D milliseconds we should be back to zero
		actualtime = t(j);
		while (t(j) < (actualtime + D))
			i(j) = 1 - (t(j) - actualtime)/D;	
			j=j+1;
		end

		// Complete the half period
		while (t(j) < (P*1000/2)+d)
			i(j) = 0;
			j=j+1;
		end

		// Here start the new half period
		i(j) = -1;
		actualtime = t(j);
		while (t(j) < (actualtime + D))
			i(j) = -1 + (t(j) - actualtime)/D;
			j=j+1;
		end		

		// Fill with zeros
		while(t(j) < P*1000)
			i(j) = 0;
			j=j+1;
		end

		i(j) = 0;

		i=i*M;

	end

endfunction

[i, t] = spsm_current(50, 4.5, 4);
i=i';
v = sin(6.28*50*t/1000);

scf();

subplot(1,2,1);
plot(t,v);
plot(t,i);
xtitle('Normalized Voltage and Current','ms', 'V, I');
xgrid;

k=1;
power = 0;
while (t(k) < 1/50*1000)
	power = power + v(k)*i(k)*(1/50/length(t));
	k=k+1;
end
power=power*50;

subplot(1,2,2);
plot(t,v.*i);
xtitle('Normalized Power = ' + string(power),'ms', 'W');
xgrid;

k=1;
i_rms = 0;
while (t(k) < 1/50*1000)
	i_rms = i_rms + i(k)*i(k)*(1/50/length(t));
	k=k+1;
end
i_rms = i_rms*50;
i_rms = sqrt(i_rms);

scf();
plot(t,i);
xtitle('Normalized RMS = ' + string(i_rms),'ms', 'W');
xgrid;
