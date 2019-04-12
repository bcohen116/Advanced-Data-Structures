// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the HASHINGPROJECTS_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// HASHINGPROJECTS_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.

#define HASHINGPROJECTS_API __declspec(dllexport)

#include <map>
#include <string>
#include <set>
#include <vector>
#include <iostream>
#include <mutex>


typedef std::map<std::string, std::vector<std::string>> StringToVectorStringsHashTable;

// This class is exported from the HashingProjects.dll
class HASHINGPROJECTS_API LicenseManager 
{
private:
	LicenseManager(const LicenseManager&) = delete;
	LicenseManager & operator=(const LicenseManager&) = delete;


	static std::unique_ptr<LicenseManager> instance;
	static std::once_flag onceFlag;
public:
	LicenseManager();// = default;

	static void NofityInit();
	static LicenseManager& LicenseManager::Instance();


public:


	void ReserveLicense(const std::string & context, const std::string & license);

	void ReleaseLicense(const std::string & context, const std::string & license);

	const std::vector<std::string> GetLicensesForContext(const std::string & context);

	const std::vector<std::string> GetContextForLicense(const std::string & license);

	void ReleaseContext(const std::string & context);

	void ReleaseLicense(const std::string & license);

	void  ReleaseAll();

	private:
		
		StringToVectorStringsHashTable m_ContextToLicenses;
		StringToVectorStringsHashTable m_LicenseToContext;
};


