#include "stdafx.h"
#include "CppUnitTest.h"
#include "ConvertBase.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace UnitTest1
{		
	TEST_CLASS(UnitTest1)
	{
	public:
		
		TEST_METHOD(TestInputs)
		{
			std::string retVal = ChangeBase(-1, 2);
			Assert::AreEqual("", retVal.c_str());

			retVal = ChangeBase(1, 1);
			Assert::AreEqual("", retVal.c_str());

			retVal = ChangeBase(1, 37);
			Assert::AreEqual("", retVal.c_str());
		}

		TEST_METHOD(TestBase2)
		{
			std::string retVal = ChangeBase(2017, 2);
			Assert::AreEqual("11111100001", retVal.c_str());;
		}

		TEST_METHOD(TestBase8)
		{
			std::string retVal = ChangeBase(2017, 8);
			Assert::AreEqual("3741", retVal.c_str());;
		}

		TEST_METHOD(TestBase16)
		{
			std::string retVal = ChangeBase(2017, 16);
			Assert::AreEqual("7E1", retVal.c_str());;
		}

		TEST_METHOD(TestBase32)
		{
			std::string retVal = ChangeBase(2017, 36);
			Assert::AreEqual("1K1", retVal.c_str());;
		}

	};
}