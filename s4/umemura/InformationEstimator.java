package s4.umemura; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 

import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s4.specification.*;

/* What is imported from s4.specification
package s4.specification;
public interface InformationEstimatorInterface{
    void setTarget(byte target[]); // set the data for computing the information quantities
    void setSpace(byte space[]); // set data for sample space to computer probability
    double estimation(); // It returns 0.0 when the target is not set or Target's length is zero;
// It returns Double.MAX_VALUE, when the true value is infinite, or space is not set.
// The behavior is undefined, if the true value is finete but larger than Double.MAX_VALUE.
// Note that this happens only when the space is unreasonably large. We will encounter other problem anyway.
// Otherwise, estimation of information quantity, 
}                        
*/

public class InformationEstimator implements InformationEstimatorInterface {
	// Code to tet, *warning: This code condtains intentional problem*
	byte[] myTarget; // data to compute its information quantity
	byte[] mySpace; // Sample space to compute the probability
	FrequencerInterface myFrequencer; // Object for counting frequency

	ArrayList<HashMap<String, Double>> lq = new ArrayList<>();

	byte[] subBytes(byte[] x, int start, int end) {
		// corresponding to substring of String for byte[] ,
		// It is not implement in class library because internal structure of byte[]
		// requires copy.
		byte[] result = new byte[end - start];
		for (int i = 0; i < end - start; i++) {
			result[i] = x[start + i];
		}
		;
		return result;
	}

	// IQ: information quantity for a count, -log2(count/sizeof(space))
	double iq(int freq) {
		return -Math.log10((double) freq / (double) mySpace.length) / Math.log10((double) 2.0);
	}

	public void setTarget(byte[] target) {
		myTarget = target;
	}

	public void setSpace(byte[] space) {
		myFrequencer = new Frequencer();
		mySpace = space;
		myFrequencer.setSpace(space);
	}

	public double estimation() {
		return minIq(myTarget);
	}

	public double minIq(byte[] target) {
		if (target.length == 0) {
			return 0;
		}
		if (target.length == 1) {
			return memoIq(target);
		}

		double value = Double.MAX_VALUE;
		int end = target.length;
		int start = 0;
		for (int partion = 1; partion <= end; partion++) {
			byte[] target0 = subBytes(target, start, partion);
			double value1 = memoIq(target0);
			byte[] target1 = subBytes(target, partion, end);
			value1 += minIq(target1);
			if (value > value1) {
				value = value1;
			}
		}

		return value;
	}

	public double memoIq(byte[] target) {
		double iq = 0.0;
		boolean notFound = true;
		String targetStr = new String(target);
		for (HashMap<String, Double> l : lq) {
			if (l.containsKey(targetStr)) {
				iq = l.get(targetStr);
				notFound = false;
				break;
			} else {
				notFound = true;
			}
		}
		if (notFound) {
			myFrequencer.setTarget(target);
			iq = iq(myFrequencer.frequency());
			HashMap<String, Double> item = new HashMap<String, Double>();
			item.put(targetStr, iq);
			lq.add(item);
		}
		return iq;
	}

	public static void main(String[] args) {
		InformationEstimator myObject;
		double value;
		myObject = new InformationEstimator();
		myObject.setSpace("3210321001230123".getBytes());
		myObject.setTarget("0".getBytes());
		value = myObject.estimation();
		System.out.println(">0 " + value);
		myObject.setTarget("01".getBytes());
		value = myObject.estimation();
		System.out.println(">01 " + value);
		myObject.setTarget("0123".getBytes());
		value = myObject.estimation();
		System.out.println(">0123 " + value);
		myObject.setTarget("00".getBytes());
		value = myObject.estimation();
		System.out.println(">00 " + value);
	}
}
