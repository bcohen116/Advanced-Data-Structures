// HashingProjects.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "HashingProjects.h"

std::unique_ptr<LicenseManager> LicenseManager::instance;
std::once_flag LicenseManager::onceFlag;

void LicenseManager::NofityInit()
{
	std::cout << "Initializing Singleton" << '\n';
}
LicenseManager& LicenseManager::Instance()
{
	std::call_once(LicenseManager::onceFlag, []() {
		NofityInit();
		instance.reset(new LicenseManager);
	});

	std::cout << "Getting  Singleton instance" << '\n';
	return *(instance.get());
}

LicenseManager::LicenseManager() : m_ContextToLicenses(), m_LicenseToContext()
{

}


void LicenseManager::ReserveLicense(const std::string & context, const std::string & license)
{
	m_ContextToLicenses[context].push_back(license);
	m_LicenseToContext[license].push_back(context);
}

void LicenseManager::ReleaseLicense(const std::string & context, const std::string & license)
{
	for (unsigned x = 0; m_ContextToLicenses[context].size(); x++) {
		//loop through all of the licenses attached to the context provided
		if (!m_ContextToLicenses[context].at(x).compare(license)) {
			//if license matches the request, remove it
			m_ContextToLicenses[context].erase(m_ContextToLicenses[context].begin() + (x));
			break;
		}
	}
	for (unsigned y = 0; m_LicenseToContext[license].size(); y++) {
		//loop through all of the contexts attached to the license provided
		if (m_LicenseToContext[license].size() > 0 && !m_LicenseToContext[license].at(y).compare(context)) {
			//if license matches the request, remove it
			m_LicenseToContext[license].erase(m_LicenseToContext[license].begin() + (y));
			break;
		}
	}
}

const std::vector<std::string> LicenseManager::GetLicensesForContext(const std::string & context)
{
	if (m_ContextToLicenses.find(context) != m_ContextToLicenses.end()){
		return m_ContextToLicenses[context];
	}
	return std::vector<std::string>();
}

const std::vector<std::string> LicenseManager::GetContextForLicense(const std::string & license)
{
	if (m_LicenseToContext.find(license) != m_LicenseToContext.end()) {
		return m_LicenseToContext[license];
	}
	return std::vector<std::string>();
}

void LicenseManager::ReleaseContext(const std::string & context)
{
	std::vector<std::string> licenses = GetLicensesForContext(context);
	for (int x = 0; x < licenses.size();x++) {
		ReleaseLicense(context, licenses.at(x));
	}
}

void LicenseManager::ReleaseLicense(const std::string & license)
{
	for (int x = 0; m_LicenseToContext[license].size(); x++) {
		//loop through all of the contexts attached to the license provided
		
		//remove all references to the license provided
		ReleaseLicense(m_LicenseToContext[license].at(x), license);
	}
}

void  LicenseManager::ReleaseAll()
{
	this->m_ContextToLicenses.clear();
	this->m_LicenseToContext.clear();
}
