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
    
    public partial class Qualifications
    {
        public int Id { get; set; }
        public string Observation { get; set; }
        public int Qualification_criteria { get; set; }
        public int AssesmentCriteriaId { get; set; }
        public int AssessmentCriteriaId { get; set; }
        public int ChecklistId { get; set; }
        public bool State { get; set; }
        public System.DateTime Created_at { get; set; }
        public Nullable<System.DateTime> Updated_at { get; set; }
        public Nullable<System.DateTime> Deleted_at { get; set; }
    
        public virtual AssessmentCriterias AssessmentCriterias { get; set; }
        public virtual Checklists Checklists { get; set; }
    }
}
