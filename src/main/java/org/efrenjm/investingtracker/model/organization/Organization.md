```java
interface Organization {
	ObjectId id;
	String name;
	String description;
	List<UserRole> members;
	User createdBy;
	Date createdAt;
	Date updatedAt;
	List<Account> accounts;
}
    interface UserRole {
        User user;
        Permissions permissions;
    }
        interface Permissions {
            Boolean createAccounts;
            Boolean createTransactions;
            Boolean createRules;
            Boolean addUsers;
            Boolean editOrganization;
            Boolean editAccounts;
            Boolean editTransactions;
            Boolean editRules;
            Boolean editUserRoles;
            Boolean viewAccounts;
            Boolean viewTransactions;
            Boolean viewRules;
            Boolean viewUserRoles;
            Boolean deleteOrganization;
            Boolean deleteAccounts;
            Boolean deleteTransactions;
            Boolean deleteRules;
            Boolean removeUsers;
        }

		
    interface Account {
        String name;
        String description;
        String type;
        Double available;
        List<String> tags;
        Date createdAt;
        Date updatedAt;
        AccountConfig configuration;
        List<Rule> rules;
        Double goal;
        String asset;
        Double currentPrice;
        Double unitCost;
        Double currentDebt;
        Double creditLimit;
    }
	    interface AccountConfig {
		    String color;
		    String icon;
		    Boolean visible;
		    String image;
		    Boolean includedInNetSum;
		    String group;
        }
```