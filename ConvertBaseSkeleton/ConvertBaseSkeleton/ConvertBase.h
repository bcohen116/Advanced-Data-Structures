#pragma once

#include <string>

// ChangeBase
// p is an integer in base 10
// b is the integer base to return
// 
// p must be in range of 0 <= p <= 9,000
// b must be in range of 2<= b <=36
// If one or neither of these are satisifed
// the empty string is passed back.
//
// This will return a string with the number p
// represented in base b.  The letters A-Z(note uppercase)
// will be for additional digits in the appropiate base
// 
extern __declspec(dllexport)  std::string ChangeBase(int p,int b);
