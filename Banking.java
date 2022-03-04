import java.util.ArrayList;
import java.util.InputMismatchException;
import java.io.IOException;  
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.ws.Response;
class Banking {
    static Registration[] registered_Users=new Registration[10];
    static int users=0;//total no. of users registered
    static Scanner sc=new Scanner(System.in);//making Scanner object static so we can use it for all inputs

    public static void main(String args[]){
    
        mainMenu();//calling mainMenu method
        
    }

    public static void mainMenu(){
            ClearScreen();//calling to clear console screen

            System.out.print("\t\t\t\t\t\t\t\t\t\t\tWelcome to World Bank\n\n\nPlease select the type of user you want to proceed with :" +
                                "\n1. Bank Agent\n2. Old User\n3. New User\n4. Exit\n\nChoice : ");

            try{//in case user provide String or character type input instead of number

                    int user_type=sc.nextInt();
                    //out:
                    switch(user_type){
                        case 1 :    adminLogin();
                                  
                        case 2 :   
                                    int id=userLogin();
                                    if(id==-1){
                                        System.out.print("\n*Please wait for Bank Agent to approve your registration"); 
                                    }
                                    else{
                                        if(id==-2){
                                            System.out.print("\n*Invalid Login Credentials");
                                        }
                                        else{
                                            userFunctionality(id);
                                        }
                                    }    
                                
                        case 3 :    
                                    boolean isValidContact=false;
                                    String contact="";
                                    
                            
                                    Scanner registration_details=new Scanner(System.in);//Creating Scanner class object
                                    System.out.print("Enter your Name : ");
                                    String name= registration_details.nextLine();
                            
                                    do{
                                        System.out.print("\nEnter your Mobile Number : ");
                                        contact=registration_details.nextLine();
                                        isValidContact=isValidMobileNumber(contact);
                            
                                        if(!isValidMobileNumber(contact)){
                                            System.out.println("*Invalid mobile number, please re-enter");
                                        }
                                    }
                                    while(isValidContact==false);
                        
                            
                                        System.out.print("\nEnter your Username : ");
                                        String username=registration_details.nextLine();
                            
                                        System.out.print("\nEnter your Password : ");
                                        String password=registration_details.nextLine();
                                        registered_Users[users]=new Registration(users, name, contact, username, password);
                                        users++;
                                        System.out.println("*You have been registered successfull, wait for approval.\n\n1.Login\n2.Exit");
                                        int choice=sc.nextInt();
                                        switch(choice){
                                            case 1 :  
                                                    int userid=userLogin();
                                                    if(userid==-1){
                                                        System.out.print("\n*Please wait for Bank Agent to approve your registration"); 
                                                    }
                                                    else{
                                                        if(userid==-2){
                                                            System.out.print("\n*Invalid Login Credentials");
                                                        }
                                                        else{
                                                            userFunctionality(userid);
                                                        }
                                                    }

                                                        
                                            case 2 : mainMenu();
                                        }
                                      
                        case 4 :    break;
                        default:    System.out.println("*Please enter a valid choice, wait for 2 seconds for application to restart.");
                                    WaitForWhile();//calling method to put thread in sleep 
                                    mainMenu();//main method will be called after 2s

                    }
            }
            catch (InputMismatchException ex) {
                System.out.println("Wrong Input! Please enter only numbers,wait for 2 seconds for application to restart.");
                WaitForWhile();//calling method to put thread in sleep 
                mainMenu();//main method will be called after 2s
            }    

    }


    public static void ClearScreen(){//method for clearing console screen
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();  
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static boolean isValidMobileNumber(String contact){//method for validating mobile number
        Pattern ptrn = Pattern.compile("(0/91)?[7-9][0-9]{9}"); //Pattern and Matcher class belongs to java.util.regex package
        Matcher match = ptrn.matcher(contact);  
        return (match.find() && match.group().equals(contact));  
    }

   

    public static void WaitForWhile() {//method to put thread in sleep for a while
        try{
            Thread.sleep(2000);
        }
        catch(Exception e){System.out.println(e);}    
    }


    public static void adminLogin(){
        String response="";
        System.out.print("\t\t\t\t\t\t\t\tLogin\n\nEnter your Username : ");
        String admin_loginId=sc.next();
        System.out.print("\nEnter your Password : ");
        String admin_password=sc.next();
        response=BankAgent.login(admin_loginId,admin_password);
        if(response.equals("Pass")){
            displayAdminChoice();
        }
        else{
            System.out.print("\n"+response+",wait for 2 seconds to try again.");
            WaitForWhile();//wait for 2 seconds to display admin_loginResponse
            adminLogin();//calling adminLogin method if login failed
        }
    }


   

    public static void displayAdminChoice(){

        System.out.print("\n\nPlease select your choice : \n1.All User List\n2.Unapproved User List\n3. Exit\n\nChoice : ");
        int admin_choice=sc.nextInt();
        switch(admin_choice){
            case 1 :    if(users==0){
                            System.out.print("*Oop! No user has been registered, wait for 2 seconds then try again");
                         
                        }
                        else{
                            BankAgent b1=new BankAgent();
                            b1.displayAllUSers(registered_Users,users);
                            System.out.print("\n\nSelect UserID to give Approval :  ");
                            int approve_userId=sc.nextInt();
                            String approve_status=b1.approveUser(registered_Users, approve_userId, users);
                            if(approve_status.equals("updated")){
                                System.out.print("------------------------------------------------\n");
                                b1.displayAllUSers(registered_Users,users);//changing user status and instantly displaying updation
                            }
                            else{
                                System.out.print("*Some error occured while updating user status");
                            }
                        }
                        displayAdminChoice();

            case 2 :    if(users==0){
                            System.out.print("*Oop! No user has been registered, wait for 2 seconds then try again.");
                        }
                        else{
                            BankAgent b2=new BankAgent(); 
                            String unapprovedUsers_response=b2.displayUnapprovedUsers(registered_Users, users);
                            if(unapprovedUsers_response==null){
                                System.out.print("\n*No user is pending for approval");
                            }
                            else{
                                System.out.print("\n\nSelect UserID to give Approval :  ");
                                int approve_userId=sc.nextInt();
                                String approve_status=b2.approveUser(registered_Users, approve_userId, users);
                                if(approve_status.equals("updated")){
                                    System.out.print("------------------------------------------------\n");
                                    b2.displayAllUSers(registered_Users,users);//changing user status and instantly displaying updation
                                }
                                else{
                                    System.out.print("*Some error occured while updating user status");
                                }
                            }
                           
                        }
                        displayAdminChoice();

            case 3 : mainMenu();
        }

    }

    public static int userLogin(){
        int userid;
        System.out.print("\n\n\n\t\t\t\t\t\t\t\t\t\t\t\tLogin\n\nEnter your Username : ");
        String user_Name=sc.next();
        System.out.print("\nEnter your Password : ");
        String user_Password=sc.next();
        userid=Registration.login(registered_Users,users, user_Name,user_Password);
        return userid;
    }


    public static void userFunctionality(int id){
        int choice;
        System.out.print("\n\nPlease select your choice : \n1.Withdrawl\n2.Deposit\n3.Check Balance\n4.Apply Loan\n5.Exit\n\nChoice : ");
        choice=sc.nextInt();
        switch(choice){
            case 1 :    
                        String withdrawlResponse=Registration.withdraw(registered_Users,users,id);
                        if(withdrawlResponse.equals("pass")){
                            System.out.print("\nCurrent Balance : "+Registration.getCurrentBalance(registered_Users,users,id));
                            WaitForWhile();
                        }
                        else{
                            System.out.print("\n"+withdrawlResponse);
                        }
                        userFunctionality(id);

            case 2 :     String depositResponse=Registration.deposit(registered_Users,users,id);
                        if(depositResponse.equals("pass")){
                            System.out.print("\nCurrent Balance : "+Registration.getCurrentBalance(registered_Users,users,id));
                            WaitForWhile();
                        }
                        else{
                             System.out.print("\n"+depositResponse);
                         }

                        userFunctionality(id);

            case 3 :    double account_balance=Registration.getCurrentBalance(registered_Users,users,id);
                        System.out.print("\nCurrent Balance : "+account_balance);    
                        WaitForWhile();
                        userFunctionality(id);

            case 4 :    String loan_response=Registration.applyLoan(registered_Users,users,id);
                        System.out.print("\n"+loan_response);
                        userFunctionality(id);

            case 5 :    mainMenu();
        }

    }
    
}


 class Registration {
    public int registration_id;
    public String name;
    public String phone_number;
    public String status;//declaring it public so that we can use it in Bank Agent class in order to check the status of users
    public double current_balance;
    public String login_id;
    public String password;

        Registration(int id,String  name, String contact,String loginid,String password){//method for setting the field values of Registation object

            this.registration_id=id;
            this.name=name;
            this.phone_number=contact;
            this.status="Pending";//default status of new registered user
            this.current_balance=0.0d;//initial current balance of user
            this.login_id=loginid;
            this.password=password;
        } 


        public static int login(Registration[] users,int user,String username,String password){
           
            int id=0;
            for(int i=0;i<user;i++){
                if(users[i].login_id.equals(username) && users[i].password.equals(password)){
                    if(users[i].status.equals("Approved")){
                        id=users[i].registration_id;//assigning id of user if login credentials are valid
                        break;
                    }
                    else{
                        id=-1;//assigning -1 if user login credentials are valid but status is pending
                        break;
                    }
                }
                else{
                    id=-2;//assigning -2 if login credentials are invalid
                   continue;
                }
            }
          

            return id;
        }

        public static String withdraw(Registration[] users,int total_users,int id){//method for amount withdrawl
            double amount;
            String response="";
            for(int i=0;i<total_users;i++){
                if(users[i].registration_id==id){
                    System.out.print("\nEnter Amount : ");
                    amount=Banking.sc.nextDouble();
                    if(users[i].current_balance>amount){
                        users[i].current_balance-=amount;
                        response="pass";
                        break;
                    }
                    else{
                        response="Low balance";
                        break;
                    }
                }
                else{
                    response="fail";
                    continue;//continue with next user
                }
            }
            return response;
        }

        public static String deposit(Registration[] users,int total_users,int id){//method for amount deposit
            double amount;
            String response="";
            for(int i=0;i<total_users;i++){
                if(users[i].registration_id==id){
                    System.out.print("\nEnter Amount : ");
                    amount=Banking.sc.nextDouble();
                    users[i].current_balance+=amount;
                    response="pass";
                    break;
                }
                else{
                    response="fail";
                    continue;//continue with next user
                }
            }
            return response;
        }


        public static double getCurrentBalance(Registration[] users,int total_users,int id){//method for returning current balance
            double balance=0.0;
            for(int i=0;i<total_users;i++){
                if(users[i].registration_id==id){
                   
                   balance= users[i].current_balance;
                   break;
                }
                else{
                    continue;//continue with next user
                }
            }
            return balance;
        }


        public static String applyLoan(Registration[] users,int total_users,int id){
            double loan_amount=0.0;
            double balance=0.0;
            String response="";
            double loan_limit;
            double interest;

            System.out.print("\nEnter loan amount : ");
            loan_amount=Banking.sc.nextDouble();
            balance=getCurrentBalance(users,total_users,id);
            if(loan_amount>balance){
                response="\n*Sorry!You are not eligible for loan";
            }
            else{
                System.out.print("\nSelect the type of loan required : \n\n1.Personal\n2.Home\n\nChoice : ");
                int choice=Banking.sc.nextInt();
                switch(choice){
                    case 1 :    
                            interest=((loan_amount*7)/100);//7 % of loan amount
                            if(loan_amount<balance+interest){
                                response="Congrats! your loan application has been sanctioned";
                            }
                            else{
                                if(loan_amount==balance+interest){
                                    loan_limit=balance-interest;
                                    response="Congrats! your loan application has been sanctioned with loan limit : "+loan_limit;
                                }
                            }
                            break;

                    case 2 :    
                            interest=((loan_amount*8)/100);//8 % of loan amount
                            if(loan_amount<balance+interest){
                                response="Congrats! your loan application has been sanctioned";
                            }
                            else{
                                if(loan_amount==balance+interest){
                                    loan_limit=balance-interest;
                                    response="Congrats! your loan application has been sanctioned with loan limit : "+loan_limit;
                                }
                            }  
                            
                            break;

                    default :   System.out.print("*Please enter valid input ");
                                Banking.WaitForWhile();    
                                Banking.userFunctionality(id);
                }

            }

            return response;
        }

}


class BankAgent {
    public void displayAllUSers(Registration[] users, int total_users){//for displaying all registered users
       
        for(int i=0;i<total_users ;i++){
            System.out.println("ID : "+users[i].registration_id+"\n Name : "+users[i].name+"\nMobile No. : "+users[i].phone_number+"\nStatus : "+
            users[i].status+"\nLogin ID : "+users[i].login_id+"\nPassword : "+users[i].password+"\nCurrent Balance : "+users[i].current_balance+"\n");
        }
    } 

    public String displayUnapprovedUsers(Registration[] users,int total_users){//method for displaying all unapproved users
        String response="";
        for(int i=0;i<total_users ;i++){
            if(users[i].status.equals("Pending")){
                System.out.println("ID : "+users[i].registration_id+"\n Name : "+users[i].name+"\nMobile No. : "+users[i].phone_number+"\nStatus : "+
                users[i].status+"\nLogin ID : "+users[i].login_id+"\nPassword : "+users[i].password+"\nCurrent Balance : "+users[i].current_balance);
            }
            else{
               response=null;
            }
        }

        return response;
    }

    public String approveUser(Registration[] users, int userId,int total_users){//for giving approvance to user status which are pending(default)
        String response="";
        for(int i=0;i<total_users;i++){
           if(users[i].registration_id==userId){
               users[i].status="Approved";
               response="updated";
               break;
           }
           else{
               response="error";
               continue;//continue with next user
           }
        }

        return response;
    }

    public static String login(String username,String password){//implementing abstract method of Login class
           
        String answer="";
        if(username.equals("admin") && password.equals("admin")){
            answer="Pass";
        }
        else{
            answer="Invalid Login Credentials";

        } 
        return answer;
    }
    
}

