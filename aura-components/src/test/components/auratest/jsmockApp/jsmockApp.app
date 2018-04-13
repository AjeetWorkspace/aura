<aura:application
		model="java://org.auraframework.components.test.java.model.TestJavaModel"
		controller="java://org.auraframework.components.test.java.controller.JavaTestController"
		provider="java://org.auraframework.test.java.provider.EmptyConfigProvider">
	<auraStorage:init name="actions" secure="true" debugLoggingEnabled="true" defaultExpiration="60" defaultAutoRefreshInterval="60"/>
	<aura:attribute name="providedAttribute" type="String"/>
	<ui:button aura:id="trigger" press="{!c.getServerString}" label="get a string"/>
	<div aura:id="output">{!v.providedAttribute}{!m.secret}<aura:iteration items="{!m.stringList}" var="x" start="{!m.integer}" end="{!m.integerString}">{!x}</aura:iteration></div>
</aura:application> 