package s4.B193381;

import java.lang.*;
import java.text.StringCharacterIterator;

import s4.specification.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/*package s4.specification;
  ここは、１回、２回と変更のない外部仕様である。
  public interface FrequencerInterface {     // This interface provides the design for frequency counter.
  void setTarget(byte  target[]); // set the data to search.
  void setSpace(byte  space[]);  // set the data to be searched target from.
  int frequency(); //It return -1, when TARGET is not set or TARGET's length is zero
  //Otherwise, it return 0, when SPACE is not set or SPACE's length is zero
  //Otherwise, get the frequency of TAGET in SPACE
  int subByteFrequency(int start, int end);
  // get the frequency of subByte of taget, i.e target[start], taget[start+1], ... , target[end-1].
  // For the incorrect value of START or END, the behavior is undefined.
  }
*/

public class Frequencer implements FrequencerInterface {
	// Code to start with: This code is not working, but good start point to work.
	byte[] myTarget;
	byte[] mySpace;
	boolean targetReady = false;
	boolean spaceReady = false;

	int[] suffixArray; // Suffix Arrayの実装に使うデータの型をint []とせよ。
	ArrayList<String> suffixStrArray;
	String myTargetStr;

	// The variable, "suffixArray" is the sorted array of all suffixes of mySpace.
	// Each suffix is expressed by a integer, which is the starting position in
	// mySpace.

	// The following is the code to print the contents of suffixArray.
	// This code could be used on debugging.

	private void printSuffixArray() {
		if (spaceReady) {
			for (int i = 0; i < suffixStrArray.size(); i++) {
				System.out.println(suffixStrArray.get(i));
			}
		}
	}

	public void setSpace(byte[] space) {
		// suffixArrayの前処理は、setSpaceで定義せよ。
		mySpace = space;
		if (mySpace.length > 0)
			spaceReady = true;
		// First, create unsorted suffix array.
		suffixArray = new int[space.length];
		String spaceStr = new String(mySpace);
		suffixStrArray = new ArrayList<>();
		// put all suffixes in suffixArray.
		for (int i = 0; i < spaceStr.length(); i++) {
			suffixArray[i] = i; // Please note that each suffix is expressed by one integer.
			suffixStrArray.add(spaceStr.substring(i));
		}
		//
		// ここに、int suffixArrayをソートするコードを書け。
		suffixStrArray.sort(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
	}

	// Suffix Arrayを用いて、文字列の頻度を求めるコード
	// ここから、指定する範囲のコードは変更してはならない。

	public void setTarget(byte[] target) {
		myTarget = target;
		myTargetStr = new String(myTarget);
		if (myTarget.length > 0 && myTargetStr.length() > 0)
			targetReady = true;
	}

	public int frequency() {
		if (targetReady == false)
			return -1;
		if (spaceReady == false)
			return 0;
		return subByteFrequency(0, myTarget.length);
	}

	public int subByteFrequency(int start, int end) {
		/*
		 * This method be work as follows, but much more efficient int spaceLength =
		 * mySpace.length; int count = 0; for(int offset = 0; offset< spaceLength - (end
		 * - start); offset++) { boolean abort = false; for(int i = 0; i< (end - start);
		 * i++) { if(myTarget[start+i] != mySpace[offset+i]) { abort = true; break; } }
		 * if(abort == false) { count++; } }
		 */

		myTargetStr = myTargetStr.substring(start, end);
		int first = subByteStartIndex(start, end);
		int last1 = subByteEndIndex(start, end);
		// System.out.println(first + " " + last1);
		return last1 - first;
	}
	// 変更してはいけないコードはここまで。

	private int subByteStartIndex(int start, int end) {
		// suffix arrayのなかで、目的の文字列の出現が始まる位置を求めるメソッド
		// 以下のように定義せよ。
		/*
		 * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
		 * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
		 */

		// It returns the index of the first suffix
		// which is equal or greater than target_start_end.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 5.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho ", it will return 6.
		//
		// ここにコードを記述せよ。
		//
		int startMatchIndex = -1;
		// for (int i = 0; i < suffixStrArray.length; i++) {
		// if (targetCompare(suffixArray[i], start, end) == 0 ||
		// targetCompare(suffixArray[i], start, end) == 1) {
		// startMatchIndex = i;
		// break;
		// }
		// }
		for (int i = 0; i < suffixStrArray.size(); i++) {
			if (suffixStrArray.get(i).compareTo(myTargetStr) >= 0) {
				startMatchIndex = i;
				break;
			}
		}
		if (startMatchIndex != -1) {
			return startMatchIndex;
		}
		return suffixStrArray.size(); // このコードは変更しなければならない。
	}

	private int subByteEndIndex(int start, int end) {
		// suffix arrayのなかで、目的の文字列の出現しなくなる場所を求めるメソッド
		// 以下のように定義せよ。
		/*
		 * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
		 * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
		 */
		// It returns the index of the first suffix
		// which is greater than target_start_end; (and not equal to target_start_end)
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 7 for "Hi Ho Hi Ho".
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is"i", it will return 9 for "Hi Ho Hi Ho".
		//
		// ここにコードを記述せよ

		int endMatchIndex = -1;
		for (int i = 0; i < suffixStrArray.size(); i++) {
			if (suffixStrArray.get(i).compareTo(myTargetStr) > 0 && !suffixStrArray.get(i).startsWith(myTargetStr)) {
				endMatchIndex = i;
				break;
			}
		}
		if (endMatchIndex != -1) {
			return endMatchIndex;
		}
		return suffixStrArray.size(); // この行は変更しなければならない、
	}

	// Suffix Arrayを使ったプログラムのホワイトテストは、
	// privateなメソッドとフィールドをアクセスすることが必要なので、
	// クラスに属するstatic mainに書く方法もある。
	// static mainがあっても、呼びださなければよい。
	// 以下は、自由に変更して実験すること。
	// 注意：標準出力、エラー出力にメッセージを出すことは、
	// static mainからの実行のときだけに許される。
	// 外部からFrequencerを使うときにメッセージを出力してはならない。
	// 教員のテスト実行のときにメッセージがでると、仕様にない動作をするとみなし、
	// 減点の対象である。
	public static void main(String[] args) {
		Frequencer frequencerObject;
		try {
			frequencerObject = new Frequencer();
			frequencerObject.setSpace("Hi Ho Hi Ho".getBytes());
			/*
			 * Example from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi Ho 4:Hi Ho Hi Ho
			 * 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
			 */

			frequencerObject.setTarget("H".getBytes());
			//
			// **** Please write code to check subByteStartIndex, and subByteEndIndex
			//

			int result = frequencerObject.frequency();
			frequencerObject.printSuffixArray(); // you may use this line for DEBUG
			System.out.print("Freq = " + result + " ");
			if (4 == result) {
				System.out.println("OK");
			} else {
				System.out.println("WRONG");
			}

		} catch (Exception e) {
			System.out.println("STOP");
		}
	}
}
