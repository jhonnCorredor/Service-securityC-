//------------------------------------------------------------------------------
// <auto-generated>
//     Este código se generó a partir de una plantilla.
//
//     Los cambios manuales en este archivo pueden causar un comportamiento inesperado de la aplicación.
//     Los cambios manuales en este archivo se sobrescribirán si se regenera el código.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Diagram
{
    using System;
    using System.Collections.Generic;
    
    public partial class Fumigations
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public Fumigations()
        {
            this.FumigationSupplies = new HashSet<FumigationSupplies>();
        }
    
        public int Id { get; set; }
        public System.DateTime DateFumigation { get; set; }
        public string QuantityMix { get; set; }
        public int ReviewTechnicalId { get; set; }
        public bool State { get; set; }
        public System.DateTime Created_at { get; set; }
        public Nullable<System.DateTime> Updated_at { get; set; }
        public Nullable<System.DateTime> Deleted_at { get; set; }
    
        public virtual ReviewTechnicals ReviewTechnicals { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<FumigationSupplies> FumigationSupplies { get; set; }
    }
}
