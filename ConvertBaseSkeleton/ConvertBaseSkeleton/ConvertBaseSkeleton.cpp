// ConvertBaseSkeleton.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "ConvertBase.h"
#include <iostream>
using namespace std;

/// <summary>Input an int and a base to convert it to. This method will return the conversion result.</summary>
extern std::string ChangeBase(int p, int b)
{
	std::string retVal = "";

	//input p is the number to convert
	//input b is base to convert p by
	if (p < 0 || p > 9000)
	{
		//input value p is out of range
		return retVal;
	}
	else if (b < 2 || b > 36)
	{
		//input value b out of range
		return retVal;
	}
	else {
		//Inputs are good, continue to conversion
		int div = p / b; //Divide p by b to get the division
		int mod = p % b; //Get the remainder for use in the end result

		//p will change every recursion based on the value of div
		if (p > 0) {
			//If there are still divisions left to be made do the following
			if (mod > 9) {
				//Convert all numbers above 10 into letters
				char hexCode = mod+55;//Since we know all of these are > 10, we can just add 55 to get the ascii code
				return ChangeBase(div, b) + hexCode;//recursive call to do another division + add in the hex letter we calculated above
			}
			else {
				//Return results from current operation
				return ChangeBase(div, b) + std::to_string(mod);//recursive call to do another division
			}
		}
	}

	return retVal;//empty return that will happen once all the recursion is finished and we have the final result
}

